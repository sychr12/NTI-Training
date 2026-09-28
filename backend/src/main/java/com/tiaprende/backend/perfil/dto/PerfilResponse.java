package com.tiaprende.backend.perfil.dto;

public record PerfilResponse(
    Long id,
    String login,
    String nome,
    String email,
    String perfil,
    PerfilEstatisticasResponse estatistica
) {
}