package com.tiaprende.backend.conquista.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tiaprende.backend.conquista.dto.ConquistasResponse.Sequencia;

class ConquistaServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 3, 10);

    private static Sequencia sequencia(int... diasAtras) {
        return ConquistaService.calcularSequencia(
                java.util.Arrays.stream(diasAtras).mapToObj(HOJE::minusDays).toList(), HOJE);
    }

    @Test
    void contaDiasSeguidosAteHoje() {
        assertThat(sequencia(0, 1, 2, 5)).isEqualTo(new Sequencia(3, 3, true));
    }

    @Test
    void sequenciaContinuaSeOUltimoEstudoFoiOntem() {
        assertThat(sequencia(1, 2)).isEqualTo(new Sequencia(2, 2, false));
    }

    @Test
    void sequenciaZeraDepoisDeUmDiaSemEstudo() {
        Sequencia resultado = sequencia(2, 3, 4, 5);
        assertThat(resultado.atual()).isZero();
        assertThat(resultado.melhor()).isEqualTo(4);
    }

    @Test
    void semAtividadeNaoHaSequencia() {
        assertThat(ConquistaService.calcularSequencia(List.of(), HOJE)).isEqualTo(new Sequencia(0, 0, false));
    }

    @Test
    void nivelSobeConformeOXp() {
        assertThat(ConquistaService.calcularNivel(0).nome()).isEqualTo("Iniciante");
        assertThat(ConquistaService.calcularNivel(99).xpProximo()).isEqualTo(100);
        assertThat(ConquistaService.calcularNivel(100).nome()).isEqualTo("Aprendiz");
        assertThat(ConquistaService.calcularNivel(5000).nome()).isEqualTo("Mestre");
        assertThat(ConquistaService.calcularNivel(5000).xpProximo()).isNull();
    }
}
