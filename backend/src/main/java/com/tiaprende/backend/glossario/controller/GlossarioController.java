package com.tiaprende.backend.glossario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.glossario.dto.TermoGlossarioRequest;
import com.tiaprende.backend.glossario.dto.TermoGlossarioResponse;
import com.tiaprende.backend.glossario.service.GlossarioService;

@RestController
@RequestMapping("/api/glossario")
public class GlossarioController {

    private final GlossarioService glossarioService;

    public GlossarioController(
            GlossarioService glossarioService
    ) {
        this.glossarioService =
                glossarioService;
    }

    @GetMapping
    public List<TermoGlossarioResponse> listar(
            @RequestParam(required = false)
            String busca
    ) {

        return glossarioService.listar(busca);
    }

    @GetMapping("/{id}")
    public TermoGlossarioResponse buscar(
            @PathVariable Long id
    ) {

        return glossarioService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TermoGlossarioResponse> criar(
            @RequestBody TermoGlossarioRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        glossarioService.criar(request)
                );
    }

    @PutMapping("/{id}")
    public TermoGlossarioResponse atualizar(
            @PathVariable Long id,
            @RequestBody TermoGlossarioRequest request
    ) {

        return glossarioService.atualizar(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id
    ) {

        glossarioService.remover(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}