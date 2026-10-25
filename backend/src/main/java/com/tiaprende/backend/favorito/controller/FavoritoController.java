package com.tiaprende.backend.favorito.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiaprende.backend.favorito.dto.FavoritoResponse;
import com.tiaprende.backend.favorito.service.FavoritoService;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(
            FavoritoService favoritoService
    ) {
        this.favoritoService = favoritoService;
    }

    /*
     * GET /api/favoritos
     */
    @GetMapping
    public List<FavoritoResponse> listar(
            @AuthenticationPrincipal SessionUser usuario
    ) {
        return favoritoService.listar(usuario.id());
    }

    /*
     * PUT /api/favoritos/1  (idempotente)
     */
    @PutMapping("/{tutorialId}")
    public ResponseEntity<Void> adicionar(
            @AuthenticationPrincipal SessionUser usuario,
            @PathVariable Long tutorialId
    ) {
        favoritoService.adicionar(usuario.id(), tutorialId);
        return ResponseEntity.noContent().build();
    }

    /*
     * DELETE /api/favoritos/1
     */
    @DeleteMapping("/{tutorialId}")
    public ResponseEntity<Void> remover(
            @AuthenticationPrincipal SessionUser usuario,
            @PathVariable Long tutorialId
    ) {
        favoritoService.remover(usuario.id(), tutorialId);
        return ResponseEntity.noContent().build();
    }
}
