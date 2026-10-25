package com.tiaprende.backend.perfil.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiaprende.backend.perfil.dto.AtualizarPreferenciasRequest;
import com.tiaprende.backend.perfil.dto.PreferenciasResponse;
import com.tiaprende.backend.perfil.repository.PreferenciasRepository;

@Service
public class PreferenciasService {

    private final PreferenciasRepository preferenciasRepository;

    public PreferenciasService(
            PreferenciasRepository preferenciasRepository
    ) {
        this.preferenciasRepository = preferenciasRepository;
    }

    public PreferenciasResponse buscar(Long usuarioId) {
        return preferenciasRepository.findByUsuario(usuarioId)
                .orElse(PreferenciasResponse.PADRAO);
    }

    @Transactional
    public PreferenciasResponse atualizar(Long usuarioId, AtualizarPreferenciasRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Dados das preferencias sao obrigatorios.");
        }

        PreferenciasResponse atual = buscar(usuarioId);
        PreferenciasResponse novas = new PreferenciasResponse(
                request.lembretesEstudo() != null ? request.lembretesEstudo() : atual.lembretesEstudo(),
                request.notificacoesEmail() != null ? request.notificacoesEmail() : atual.notificacoesEmail());

        preferenciasRepository.save(usuarioId, novas);
        return novas;
    }
}
