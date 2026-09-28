package com.tiaprende.backend.login.dto;

public record LoginRequest(
    String usuario,
    String senha,
    Boolean lembrar
) {
}