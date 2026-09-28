package com.tiaprende.backend.trilha.dto;

public record TrilhaTutorialResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        String nivel,
        int ordem
) {
}