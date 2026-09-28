package com.tiaprende.backend.progresso.service;

import com.tiaprende.backend.tutorial.repository.TutorialRepository;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.progresso.dto.AtualizarProgressoRequest;
import com.tiaprende.backend.progresso.dto.ProgressoTrilhaResponse;
import com.tiaprende.backend.progresso.dto.ProgressoTutorialResponse;
import com.tiaprende.backend.progresso.model.ProgressoTutorial;
import com.tiaprende.backend.progresso.repository.ProgressoRepository;

@Service
public class ProgressoService {

    private static final Set<String> STATUS_VALIDOS =
            Set.of(
                    "EM_ANDAMENTO",
                    "CONCLUIDO"
            );

    private final TutorialRepository tutorialRepository;

    private final ProgressoRepository progressoRepository;

    public ProgressoService(
            ProgressoRepository progressoRepository,
            TutorialRepository tutorialRepository
    ) {
        this.tutorialRepository = tutorialRepository;
        this.progressoRepository =
                progressoRepository;
    }

    public List<ProgressoTutorialResponse> listar(
            Long usuarioId
    ) {

        return progressoRepository
                .findByUsuario(usuarioId)
                .stream()
                .map(ProgressoTutorialResponse::from)
                .toList();
    }

    public List<ProgressoTrilhaResponse> listarTrilhas(
            Long usuarioId
    ) {

        return progressoRepository
                .findProgressoTrilhas(usuarioId);
    }

    public ProgressoTutorialResponse atualizar(
            Long usuarioId,
            AtualizarProgressoRequest request
    ) {

        validar(request);
        tutorialRepository.findById(request.tutorialId())
                .orElseThrow(() -> new com.tiaprende.backend.exception.ResourceNotFoundException("Tutorial nao encontrado."));

        String status =
                request.status()
                        .trim()
                        .toUpperCase(java.util.Locale.ROOT);

        ProgressoTutorial progresso =
                progressoRepository.saveOrUpdate(
                        usuarioId,
                        request.tutorialId(),
                        status
                );

        return ProgressoTutorialResponse.from(
                progresso
        );
    }

    private void validar(
            AtualizarProgressoRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Dados do progresso sao obrigatorios."
            );
        }

        if (request.tutorialId() == null) {

            throw new IllegalArgumentException(
                    "Tutorial e obrigatorio."
            );
        }

        if (request.status() == null
                || request.status().isBlank()) {

            throw new IllegalArgumentException(
                    "Status e obrigatorio."
            );
        }

        String status =
                request.status()
                        .trim()
                        .toUpperCase(java.util.Locale.ROOT);

        if (!STATUS_VALIDOS.contains(status)) {

            throw new IllegalArgumentException(
                    "Status de progresso invalido."
            );
        }
    }
}