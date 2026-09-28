package com.tiaprende.backend.user.dto;

public record UpdateUserRequest(
    String perfil,
    Boolean ativo
) {
}
