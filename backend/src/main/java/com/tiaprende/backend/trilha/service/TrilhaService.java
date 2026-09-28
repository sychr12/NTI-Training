package com.tiaprende.backend.trilha.service;

import com.tiaprende.backend.tutorial.repository.TutorialRepository;
import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.trilha.dto.TrilhaRequest;
import com.tiaprende.backend.trilha.dto.TrilhaResponse;
import com.tiaprende.backend.trilha.dto.TrilhaTutorialResponse;
import com.tiaprende.backend.trilha.model.Trilha;
import com.tiaprende.backend.trilha.repository.TrilhaRepository;

@Service
public class TrilhaService {

    private final TutorialRepository tutorialRepository;

    private final TrilhaRepository trilhaRepository;

    public TrilhaService(
            TrilhaRepository trilhaRepository,
            TutorialRepository tutorialRepository
    ) {
        this.tutorialRepository = tutorialRepository;
        this.trilhaRepository = trilhaRepository;
    }

    public List<TrilhaResponse> listarTodas() {

        return trilhaRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TrilhaResponse buscarPorId(Long id) {

        Trilha trilha = trilhaRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Trilha nao encontrada."
                        )
                );

        return toResponse(trilha);
    }

    public TrilhaResponse criar(
            TrilhaRequest request
    ) {

        validar(request);


        Trilha trilha =
                trilhaRepository.save(request);

        return toResponse(trilha);
    }

    public TrilhaResponse atualizar(
            Long id,
            TrilhaRequest request
    ) {

        validar(request);


        Trilha trilha = trilhaRepository
                .update(id, request)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Trilha nao encontrada."
                        )
                );

        return toResponse(trilha);
    }

    public void remover(Long id) {

        boolean removida =
                trilhaRepository.deactivate(id);

        if (!removida) {

            throw new ResourceNotFoundException(
                    "Trilha nao encontrada."
            );
        }
    }

    private TrilhaResponse toResponse(
            Trilha trilha
    ) {

        List<TrilhaTutorialResponse> tutoriais =
                trilhaRepository.findTutoriais(
                        trilha.id()
                );

        return TrilhaResponse.from(
                trilha,
                tutoriais
        );
    }

    private void validar(TrilhaRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Dados da trilha sao obrigatorios."
            );
        }

        if (request.titulo() == null
                || request.titulo().isBlank()) {

            throw new IllegalArgumentException(
                    "Titulo da trilha e obrigatorio."
            );
        }

        if (request.nivel() == null
                || request.nivel().isBlank()) {

            throw new IllegalArgumentException(
                    "Nivel da trilha e obrigatorio."
            );
        }
        if (request.tutorialIds() != null) {
            if (request.tutorialIds().stream().anyMatch(tutorialId -> tutorialId == null || tutorialId <= 0)
                    || new java.util.HashSet<>(request.tutorialIds()).size() != request.tutorialIds().size()) {
                throw new IllegalArgumentException("Lista de tutoriais invalida ou duplicada.");
            }
            for (Long tutorialId : request.tutorialIds()) {
                tutorialRepository.findById(tutorialId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tutorial nao encontrado."));
            }
        }
    }
}