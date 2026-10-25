package com.tiaprende.backend.perfil.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiaprende.backend.login.session.AuthSession.SessionUser;
import com.tiaprende.backend.perfil.dto.AtualizarPreferenciasRequest;
import com.tiaprende.backend.perfil.dto.PreferenciasResponse;
import com.tiaprende.backend.perfil.service.PreferenciasService;

@RestController
@RequestMapping("/api/perfil/preferencias")
public class PreferenciasController {

    private final PreferenciasService preferenciasService;

    public PreferenciasController(
            PreferenciasService preferenciasService
    ) {
        this.preferenciasService = preferenciasService;
    }

    /*
     * GET /api/perfil/preferencias
     */
    @GetMapping
    public PreferenciasResponse buscar(
            @AuthenticationPrincipal SessionUser usuario
    ) {
        return preferenciasService.buscar(usuario.id());
    }

    /*
     * PUT /api/perfil/preferencias  { "lembretesEstudo": false }
     */
    @PutMapping
    public PreferenciasResponse atualizar(
            @AuthenticationPrincipal SessionUser usuario,
            @RequestBody AtualizarPreferenciasRequest request
    ) {
        return preferenciasService.atualizar(usuario.id(), request);
    }
}
