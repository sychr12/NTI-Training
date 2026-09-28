package com.tiaprende.backend.exercicio.service;

import com.tiaprende.backend.tutorial.repository.TutorialRepository;
import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.exercicio.dto.ExercicioRequest;
import com.tiaprende.backend.exercicio.dto.ExercicioResponse;
import com.tiaprende.backend.exercicio.dto.RespostaExercicioRequest;
import com.tiaprende.backend.exercicio.dto.ResultadoExercicioResponse;
import com.tiaprende.backend.exercicio.model.Exercicio;
import com.tiaprende.backend.exercicio.repository.ExercicioRepository;

@Service
public class ExercicioService {

    private static final Set<String> RESPOSTAS_VALIDAS =
            Set.of("A", "B", "C", "D");

    private final TutorialRepository tutorialRepository;

    private final ExercicioRepository exercicioRepository;

    public ExercicioService(
            ExercicioRepository exercicioRepository,
            TutorialRepository tutorialRepository
    ) {
        this.tutorialRepository = tutorialRepository;
        this.exercicioRepository =
                exercicioRepository;
    }

    public List<ExercicioResponse> listarPorTutorial(
            Long tutorialId
    ) {

        return exercicioRepository
                .findByTutorial(tutorialId)
                .stream()
                .map(ExercicioResponse::from)
                .toList();
    }

    public ExercicioResponse buscarPorId(Long id) {

        Exercicio exercicio =
                buscarExercicio(id);

        return ExercicioResponse.from(exercicio);
    }

    public ExercicioResponse criar(
            ExercicioRequest request
    ) {

        validar(request);
        tutorialRepository.findById(request.tutorialId())
                .orElseThrow(() -> new com.tiaprende.backend.exception.ResourceNotFoundException("Tutorial nao encontrado."));

        return ExercicioResponse.from(
                exercicioRepository.save(request)
        );
    }

    public ExercicioResponse atualizar(
            Long id,
            ExercicioRequest request
    ) {

        validar(request);
        tutorialRepository.findById(request.tutorialId())
                .orElseThrow(() -> new com.tiaprende.backend.exception.ResourceNotFoundException("Tutorial nao encontrado."));

        Exercicio exercicio =
                exercicioRepository
                        .update(id, request)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Exercicio nao encontrado."
                                )
                        );

        return ExercicioResponse.from(exercicio);
    }

    public ResultadoExercicioResponse responder(
            Long usuarioId,
            Long id,
            RespostaExercicioRequest request
    ) {

        if (request == null
                || request.resposta() == null
                || request.resposta().isBlank()) {

            throw new IllegalArgumentException(
                    "Resposta e obrigatoria."
            );
        }

        String resposta =
                request.resposta()
                        .trim()
                        .toUpperCase(java.util.Locale.ROOT);

        if (!RESPOSTAS_VALIDAS.contains(resposta)) {

            throw new IllegalArgumentException(
                    "Resposta deve ser A, B, C ou D."
            );
        }

        Exercicio exercicio =
                buscarExercicio(id);

        boolean correto =
                exercicio
                        .respostaCorreta()
                        .equalsIgnoreCase(resposta);

        exercicioRepository.salvarResposta(usuarioId, id, resposta, correto);

        return new ResultadoExercicioResponse(
                correto,
                exercicio.respostaCorreta(),
                exercicio.explicacao()
        );
    }

    public void remover(Long id) {

        if (!exercicioRepository.deactivate(id)) {

            throw new ResourceNotFoundException(
                    "Exercicio nao encontrado."
            );
        }
    }

    private Exercicio buscarExercicio(Long id) {

        return exercicioRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Exercicio nao encontrado."
                        )
                );
    }

    private void validar(
            ExercicioRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Dados do exercicio sao obrigatorios."
            );
        }

        if (request.tutorialId() == null) {
            throw new IllegalArgumentException(
                    "Tutorial e obrigatorio."
            );
        }

        if (request.pergunta() == null
                || request.pergunta().isBlank()) {

            throw new IllegalArgumentException(
                    "Pergunta e obrigatoria."
            );
        }

        if (request.alternativaA() == null || request.alternativaA().isBlank()
                || request.alternativaB() == null || request.alternativaB().isBlank()
                || request.alternativaC() == null || request.alternativaC().isBlank()
                || request.alternativaD() == null || request.alternativaD().isBlank()) {

            throw new IllegalArgumentException(
                    "Todas as alternativas sao obrigatorias."
            );
        }

        if (request.respostaCorreta() == null) {
            throw new IllegalArgumentException(
                    "Resposta correta e obrigatoria."
            );
        }

        String resposta =
                request.respostaCorreta()
                        .trim()
                        .toUpperCase(java.util.Locale.ROOT);

        if (!RESPOSTAS_VALIDAS.contains(resposta)) {
            throw new IllegalArgumentException(
                    "Resposta correta deve ser A, B, C ou D."
            );
        }
    }
}