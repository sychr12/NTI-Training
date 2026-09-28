package com.tiaprende.backend.perfil.dto;

public record PerfilEstatisticasResponse(
    int tutoriaisIniciados,
    int tutoriaisConcluidos,
    int trilhasConcluidas,
    int progressoGeral
) {
}
