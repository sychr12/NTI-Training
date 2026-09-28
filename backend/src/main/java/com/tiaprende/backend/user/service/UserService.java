package com.tiaprende.backend.user.service;

import com.tiaprende.backend.exception.ResourceNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiaprende.backend.user.dto.UpdateUserRequest;
import com.tiaprende.backend.user.dto.UserResponse;
import com.tiaprende.backend.user.enums.UserRole;
import com.tiaprende.backend.user.model.AppUser;
import com.tiaprende.backend.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> listarTodos() {

        return userRepository
                .findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse buscarPorId(Long id) {

        AppUser user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario nao encontrado."
                        )
                );

        return UserResponse.from(user);
    }

    public UserResponse atualizar(
            Long id,
            UpdateUserRequest request
    ) {

        AppUser atual = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario nao encontrado."
                        )
                );

        String perfil = request.perfil() != null
                ? validarPerfil(request.perfil())
                : atual.perfil();

        boolean ativo = request.ativo() != null
                ? request.ativo()
                : atual.ativo();

        AppUser atualizado = userRepository
                .update(
                        id,
                        perfil,
                        ativo
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario nao encontrado."
                        )
                );

        return UserResponse.from(atualizado);
    }

    private String validarPerfil(String perfil) {

        try {

            return UserRole
                    .valueOf(perfil.trim().toUpperCase(java.util.Locale.ROOT))
                    .name();

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Perfil de usuario invalido."
            );
        }
    }
}