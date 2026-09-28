package com.tiaprende.backend.trilha.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.trilha.dto.TrilhaRequest;
import com.tiaprende.backend.trilha.dto.TrilhaResponse;
import com.tiaprende.backend.trilha.service.TrilhaService;

@RestController
@RequestMapping("/api/trilhas")
public class TrilhaController {

    private final TrilhaService trilhaService;

    public TrilhaController(
            TrilhaService trilhaService
    ) {
        this.trilhaService = trilhaService;
    }

    @GetMapping
    public List<TrilhaResponse> listarTodas() {

        return trilhaService.listarTodas();
    }

    @GetMapping("/{id}")
    public TrilhaResponse buscarPorId(
            @PathVariable Long id
    ) {

        return trilhaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TrilhaResponse> criar(
            @RequestBody TrilhaRequest request
    ) {

        TrilhaResponse trilha =
                trilhaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trilha);
    }

    @PutMapping("/{id}")
    public TrilhaResponse atualizar(
            @PathVariable Long id,
            @RequestBody TrilhaRequest request
    ) {

        return trilhaService.atualizar(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id
    ) {

        trilhaService.remover(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}