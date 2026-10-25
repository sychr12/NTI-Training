package com.tiaprende.backend.conquista.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiaprende.backend.conquista.dto.ConquistasResponse;
import com.tiaprende.backend.conquista.service.ConquistaService;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;

@RestController
@RequestMapping("/api/conquistas")
public class ConquistaController {

    private final ConquistaService conquistaService;

    public ConquistaController(
            ConquistaService conquistaService
    ) {
        this.conquistaService = conquistaService;
    }

    /*
     * GET /api/conquistas  -> XP, nivel, sequencia de dias e medalhas do usuario
     */
    @GetMapping
    public ConquistasResponse carregar(
            @AuthenticationPrincipal SessionUser usuario
    ) {
        return conquistaService.carregar(usuario.id());
    }

    /*
     * POST /api/conquistas/vistas  -> o frontend ja mostrou o aviso das medalhas novas
     */
    @PostMapping("/vistas")
    public ResponseEntity<Void> marcarVistas(
            @AuthenticationPrincipal SessionUser usuario
    ) {
        conquistaService.marcarVistas(usuario.id());
        return ResponseEntity.noContent().build();
    }
}
