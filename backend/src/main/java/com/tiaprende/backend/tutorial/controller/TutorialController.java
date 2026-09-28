package com.tiaprende.backend.tutorial.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.tutorial.dto.TutorialRequest;
import com.tiaprende.backend.tutorial.dto.TutorialResponse;
import com.tiaprende.backend.tutorial.service.TutorialService;

@RestController
@RequestMapping({"/api/tutorials", "/api/tutoriais"})
public class TutorialController {
    private final TutorialService tutorialService;

    public TutorialController(
        TutorialService tutorialService
    ){
        this.tutorialService = tutorialService;
    }

    @GetMapping
    public List<TutorialResponse> listarTodos(){
        return tutorialService.listaTodos();
    }

    @GetMapping("/{id}")
    public TutorialResponse buscarPorId(
        @PathVariable Long id
    ){
        return tutorialService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TutorialResponse> criar(
        @RequestBody TutorialRequest request
    ){

        TutorialResponse tutorial = tutorialService.criar(request);

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(tutorial);
    }

    @PutMapping("/{id}")
    public TutorialResponse atualizar(
        @PathVariable Long id,
        @RequestBody TutorialRequest request
    ){

        return tutorialService.atualizar(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
        @PathVariable Long id
    ){
        tutorialService.remover(id);

        return ResponseEntity
        .noContent()
        .build();
    }
}
