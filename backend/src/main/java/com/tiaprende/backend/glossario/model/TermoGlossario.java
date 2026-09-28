package com.tiaprende.backend.glossario.model;

import java.time.Instant;

public record TermoGlossario(
        Long id,
        String termo,
        String definicao,
        String categoria,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
}