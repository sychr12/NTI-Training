package com.tiaprende.backend.perfil.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiaprende.backend.login.session.AuthSession.SessionUser;
import com.tiaprende.backend.perfil.dto.PerfilResponse;
import com.tiaprende.backend.perfil.service.PerfilService;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(
            PerfilService perfilService
    ) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PerfilResponse buscarPerfil(
            Authentication authentication
    ) {

        SessionUser usuario =
                (SessionUser)
                        authentication.getPrincipal();

        return perfilService.buscarPerfil(
                usuario.id()
        );
    }
}