package com.tiaprende.backend.perfil.service;

import com.tiaprende.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.tiaprende.backend.perfil.dto.PerfilEstatisticasResponse;
import com.tiaprende.backend.perfil.dto.PerfilResponse;
import com.tiaprende.backend.progresso.repository.ProgressoRepository;
import com.tiaprende.backend.user.model.AppUser;
import com.tiaprende.backend.user.repository.UserRepository;

@Service
public class PerfilService {

    private final UserRepository userRepository;
    private final ProgressoRepository progressoRepository;

    public PerfilService(
            UserRepository userRepository,
            ProgressoRepository progressoRepository
    ) {
        this.userRepository = userRepository;
        this.progressoRepository =
                progressoRepository;
    }

    public PerfilResponse buscarPerfil(
            Long usuarioId
    ) {

        AppUser usuario =
                userRepository
                        .findById(usuarioId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Usuario nao encontrado."
                                )
                        );

        int tutoriaisIniciados =
                progressoRepository
                        .countTutoriaisIniciados(
                                usuarioId
                        );

        int tutoriaisConcluidos =
                progressoRepository
                        .countTutoriaisConcluidos(
                                usuarioId
                        );

        int trilhasConcluidas =
                progressoRepository
                        .countTrilhasConcluidas(
                                usuarioId
                        );

        int progressoGeral =
                progressoRepository
                        .calcularProgressoGeral(
                                usuarioId
                        );

        PerfilEstatisticasResponse estatisticas =
                new PerfilEstatisticasResponse(
                        tutoriaisIniciados,
                        tutoriaisConcluidos,
                        trilhasConcluidas,
                        progressoGeral
                );

        return new PerfilResponse(
                usuario.id(),
                usuario.login(),
                usuario.nome(),
                usuario.email(),
                usuario.perfil(),
                estatisticas
        );
    }
}