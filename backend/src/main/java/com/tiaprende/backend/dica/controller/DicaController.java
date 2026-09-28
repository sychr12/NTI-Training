package com.tiaprende.backend.dica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.dica.dto.DicaRequest;
import com.tiaprende.backend.dica.dto.DicaResponse;
import com.tiaprende.backend.dica.service.DicaService;

@RestController
@RequestMapping("/api/dicas")
public class DicaController {

    private final DicaService dicaService;

    public DicaController(
            DicaService dicaService
    ) {
        this.dicaService = dicaService;
    }

    @GetMapping
    public List<DicaResponse> listar(
            @RequestParam(required = false)
            String categoria
    ) {

        return dicaService.listar(categoria);
    }

    @GetMapping("/{id}")
    public DicaResponse buscarPorId(
            @PathVariable Long id
    ) {

        return dicaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<DicaResponse> criar(
            @RequestBody DicaRequest request
    ) {

        DicaResponse dica =
                dicaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dica);
    }

    @PutMapping("/{id}")
    public DicaResponse atualizar(
            @PathVariable Long id,
            @RequestBody DicaRequest request
    ) {

        return dicaService.atualizar(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long id
    ) {

        dicaService.remover(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}