package com.tiaprende.backend.exercicio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.exercicio.dto.ExercicioRequest;
import com.tiaprende.backend.exercicio.dto.ExercicioResponse;
import com.tiaprende.backend.exercicio.dto.RespostaExercicioRequest;
import com.tiaprende.backend.exercicio.dto.ResultadoExercicioResponse;
import com.tiaprende.backend.exercicio.service.ExercicioService;

@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(
            ExercicioService exercicioService
    ) {
        this.exercicioService = exercicioService;
    }

    @GetMapping
    public List<ExercicioResponse> listar(
            @RequestParam Long tutorialId
    ) {

        return exercicioService
                .listarPorTutorial(tutorialId);
    }

    @GetMapping("/{id}")
    public ExercicioResponse buscar(
            @PathVariable Long id
    ) {

        return exercicioService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ExercicioResponse> criar(
            @RequestBody ExercicioRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        exercicioService.criar(request)
                );
    }

    @PutMapping("/{id}")
    public ExercicioResponse atualizar(
            @PathVariable Long id,
            @RequestBody ExercicioRequest request
    ) {

        return exercicioService.atualizar(
                id,
                request
        );
    }

    @PostMapping("/{id}/responder")
    public ResultadoExercicioResponse responder(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.tiaprende.backend.login.session.AuthSession.SessionUser usuario,
            @PathVariable Long id,
            @RequestBody RespostaExercicioRequest request
    ) {

        return exercicioService.responder(
                usuario.id(),
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id
    ) {

        exercicioService.remover(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}