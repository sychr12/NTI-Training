package com.tiaprende.backend.user.dto;

import java.time.Instant;

import com.tiaprende.backend.user.model.AppUser;

public record UserResponse(
    Long id,
    String login,
    String nome,
    String email,
    String perfil,
    boolean ativo,
    Instant ultimoLogin
) {

    public static UserResponse from (AppUser user){
        return new UserResponse(user.id(),
        user.login(),
        user.nome(),
        user.email(),
        user.perfil(),
        user.ativo(),
        user.ultimoLogin()
    );
    }
    
}
