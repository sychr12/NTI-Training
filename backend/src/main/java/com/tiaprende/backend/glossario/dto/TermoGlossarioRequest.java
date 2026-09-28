package com.tiaprende.backend.glossario.dto;

public record TermoGlossarioRequest(
        String termo,
        String definicao,
        String categoria
) {
}