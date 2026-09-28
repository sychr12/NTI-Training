package com.tiaprende.backend.progresso.dto;

public record AtualizarProgressoRequest(
        Long tutorialId,
        String status
) {
}