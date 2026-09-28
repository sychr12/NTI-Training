package com.tiaprende.backend.trilha.model;

import java.time.Instant;

public record Trilha(
        Long id,
        String titulo,
        String descricao,
        String nivel,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
}