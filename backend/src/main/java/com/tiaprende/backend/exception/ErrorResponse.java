package com.tiaprende.backend.exception;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String erro,
        String mensagem,
        String caminho,
        Instant timestamp
) {
}