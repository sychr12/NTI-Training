package com.tiaprende.backend.dica.model;

import java.time.Instant;

public record Dica(
        Long id,
        String titulo,
        String conteudo,
        String categoria,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
}