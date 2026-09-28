package com.tiaprende.backend.progresso.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tiaprende.backend.login.session.AuthSession.SessionUser;
import com.tiaprende.backend.progresso.dto.AtualizarProgressoRequest;
import com.tiaprende.backend.progresso.dto.ProgressoTrilhaResponse;
import com.tiaprende.backend.progresso.dto.ProgressoTutorialResponse;
import com.tiaprende.backend.progresso.service.ProgressoService;

@RestController
@RequestMapping("/api/progresso")
public class ProgressoController {

    private final ProgressoService progressoService;

    public ProgressoController(
            ProgressoService progressoService
    ) {
        this.progressoService =
                progressoService;
    }

    @GetMapping
    public List<ProgressoTutorialResponse> listar(
            Authentication authentication
    ) {

        SessionUser usuario =
                (SessionUser) authentication.getPrincipal();

        return progressoService.listar(
                usuario.id()
        );
    }

    @GetMapping("/trilhas")
    public List<ProgressoTrilhaResponse> listarTrilhas(
            Authentication authentication
    ) {

        SessionUser usuario =
                (SessionUser) authentication.getPrincipal();

        return progressoService.listarTrilhas(
                usuario.id()
        );
    }

    @PutMapping
    public ProgressoTutorialResponse atualizar(
            Authentication authentication,
            @RequestBody AtualizarProgressoRequest request
    ) {

        SessionUser usuario =
                (SessionUser) authentication.getPrincipal();

        return progressoService.atualizar(
                usuario.id(),
                request
        );
    }
}