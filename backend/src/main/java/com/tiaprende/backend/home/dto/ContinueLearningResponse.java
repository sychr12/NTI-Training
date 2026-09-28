package com.tiaprende.backend.home.dto;

public record ContinueLearningResponse(
    Long id,
    String titulo,
    String descricao,
    int progresso,
    String tipo
) {
}
