package com.tiaprende.backend.conquista.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.conquista.dto.ConquistasResponse;
import com.tiaprende.backend.conquista.dto.ConquistasResponse.Medalha;
import com.tiaprende.backend.conquista.dto.ConquistasResponse.Nivel;
import com.tiaprende.backend.conquista.dto.ConquistasResponse.Sequencia;
import com.tiaprende.backend.conquista.repository.ConquistaRepository;
import com.tiaprende.backend.conquista.repository.ConquistaRepository.Registro;
import com.tiaprende.backend.conquista.repository.ConquistaRepository.Totais;
import com.tiaprende.backend.conquista.repository.DiaEstudoRepository;
import com.tiaprende.backend.progresso.repository.ProgressoRepository;

@Service
public class ConquistaService {

    // Pontuacao de cada atividade.
    static final int XP_TUTORIAL = 50;
    static final int XP_ACERTO = 10;
    static final int XP_TRILHA = 100;

    record NivelDef(String nome, int xpMinimo) {
    }

    static final List<NivelDef> NIVEIS = List.of(
            new NivelDef("Iniciante", 0),
            new NivelDef("Aprendiz", 100),
            new NivelDef("Explorador", 300),
            new NivelDef("Especialista", 600),
            new NivelDef("Mestre", 1000));

    // Numeros que as medalhas acompanham.
    record Metricas(int tutoriais, int categorias, int acertos, int trilhas, int melhorSequencia) {
    }

    record MedalhaDef(String codigo, String titulo, String descricao, String icone, int meta, ToIntFunction<Metricas> valor) {
    }

    static final List<MedalhaDef> MEDALHAS = List.of(
            new MedalhaDef("primeiro-passo", "Primeiro passo", "Conclua seu primeiro tutorial.", "rocket", 1, Metricas::tutoriais),
            new MedalhaDef("dedicado", "Dedicado", "Conclua 5 tutoriais.", "books", 5, Metricas::tutoriais),
            new MedalhaDef("explorador", "Explorador", "Conclua tutoriais de 3 categorias diferentes.", "compass", 3, Metricas::categorias),
            new MedalhaDef("certeiro", "Certeiro", "Acerte 10 exercícios.", "target", 10, Metricas::acertos),
            new MedalhaDef("trilheiro", "Trilheiro", "Complete uma trilha inteira.", "route", 1, Metricas::trilhas),
            new MedalhaDef("constante", "Constante", "Estude 3 dias seguidos.", "flame", 3, Metricas::melhorSequencia),
            new MedalhaDef("imparavel", "Imparável", "Estude 7 dias seguidos.", "trophy", 7, Metricas::melhorSequencia));

    private final ConquistaRepository conquistaRepository;
    private final DiaEstudoRepository diaEstudoRepository;
    private final ProgressoRepository progressoRepository;

    public ConquistaService(
            ConquistaRepository conquistaRepository,
            DiaEstudoRepository diaEstudoRepository,
            ProgressoRepository progressoRepository
    ) {
        this.conquistaRepository = conquistaRepository;
        this.diaEstudoRepository = diaEstudoRepository;
        this.progressoRepository = progressoRepository;
    }

    /*
     * Calcula XP, nivel, sequencia e medalhas. Medalhas atingidas pela primeira vez
     * sao registradas aqui, com a data da conquista. Sem @Transactional de proposito: o registro
     * tolera chave duplicada (requisicoes simultaneas), o que abortaria uma transacao no PostgreSQL.
     */
    public ConquistasResponse carregar(Long usuarioId) {

        Totais totais = conquistaRepository.totais(usuarioId);
        int trilhas = progressoRepository.countTrilhasConcluidas(usuarioId);
        Sequencia sequencia = calcularSequencia(diaEstudoRepository.findDias(usuarioId), diaEstudoRepository.hoje());

        Metricas metricas = new Metricas(totais.tutoriaisConcluidos(), totais.categoriasConcluidas(),
                totais.acertos(), trilhas, sequencia.melhor());
        int xp = totais.tutoriaisConcluidos() * XP_TUTORIAL + totais.acertos() * XP_ACERTO + trilhas * XP_TRILHA;

        Map<String, Registro> registros = conquistaRepository.findByUsuario(usuarioId);
        boolean registrouNova = false;
        for (MedalhaDef def : MEDALHAS) {
            if (def.valor().applyAsInt(metricas) >= def.meta() && !registros.containsKey(def.codigo())) {
                conquistaRepository.registrar(usuarioId, def.codigo());
                registrouNova = true;
            }
        }
        if (registrouNova) {
            registros = conquistaRepository.findByUsuario(usuarioId);
        }

        List<Medalha> medalhas = new ArrayList<>();
        for (MedalhaDef def : MEDALHAS) {
            Registro registro = registros.get(def.codigo());
            int valor = def.valor().applyAsInt(metricas);
            medalhas.add(new Medalha(def.codigo(), def.titulo(), def.descricao(), def.icone(),
                    Math.min(valor, def.meta()), def.meta(), registro != null,
                    registro == null ? null : registro.conquistadaEm(),
                    registro != null && !registro.vista()));
        }

        return new ConquistasResponse(xp, calcularNivel(xp), sequencia, medalhas);
    }

    public void marcarVistas(Long usuarioId) {
        conquistaRepository.marcarVistas(usuarioId);
    }

    static Nivel calcularNivel(int xp) {
        int indice = 0;
        for (int i = 0; i < NIVEIS.size(); i++) {
            if (xp >= NIVEIS.get(i).xpMinimo()) {
                indice = i;
            }
        }
        NivelDef atual = NIVEIS.get(indice);
        Integer proximo = indice + 1 < NIVEIS.size() ? NIVEIS.get(indice + 1).xpMinimo() : null;
        return new Nivel(indice + 1, atual.nome(), atual.xpMinimo(), proximo);
    }

    /*
     * dias: datas com atividade, da mais recente para a mais antiga (sem repeticao).
     * A sequencia atual continua valendo se o ultimo estudo foi hoje ou ontem.
     */
    static Sequencia calcularSequencia(List<LocalDate> dias, LocalDate hoje) {

        int melhor = 0;
        int corrida = 0;
        LocalDate anterior = null;
        for (LocalDate dia : dias) {
            corrida = anterior != null && dia.equals(anterior.minusDays(1)) ? corrida + 1 : 1;
            melhor = Math.max(melhor, corrida);
            anterior = dia;
        }

        boolean estudouHoje = !dias.isEmpty() && dias.get(0).equals(hoje);
        int atual = 0;
        if (!dias.isEmpty() && !dias.get(0).isBefore(hoje.minusDays(1))) {
            LocalDate esperado = dias.get(0);
            for (LocalDate dia : dias) {
                if (!dia.equals(esperado)) {
                    break;
                }
                atual++;
                esperado = esperado.minusDays(1);
            }
        }

        return new Sequencia(atual, melhor, estudouHoje);
    }
}
