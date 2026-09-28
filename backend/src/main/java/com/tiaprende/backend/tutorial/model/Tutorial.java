package com.tiaprende.backend.tutorial.model;

import java.time.Instant;

public record Tutorial(
    Long id,
    String titulo,
    String descrisao,
    String conteudo,
    String categoria,
    String nivel,
    boolean ativo,
    Instant criadoEm,
    Instant atulizadoEm
) {
}
