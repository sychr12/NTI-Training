package com.tiaprende.backend.progresso.model;

import java.time.Instant;

public record ProgressoTutorial(
        Long id,
        Long usuarioId,
        Long tutorialId,
        String status,
        int percentual,
        Instant iniciadoEm,
        Instant concluidoEm,
        Instant atualizadoEm
) {
}