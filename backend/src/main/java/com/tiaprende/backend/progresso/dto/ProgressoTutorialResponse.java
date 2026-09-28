package com.tiaprende.backend.progresso.dto;

import java.time.Instant;

import com.tiaprende.backend.progresso.model.ProgressoTutorial;

public record ProgressoTutorialResponse(
        Long tutorialId,
        String status,
        Instant iniciadoEm,
        Instant concluidoEm,
        Instant atualizadoEm
) {

    public static ProgressoTutorialResponse from(
            ProgressoTutorial progresso
    ) {

        return new ProgressoTutorialResponse(
                progresso.tutorialId(),
                progresso.status(),
                progresso.iniciadoEm(),
                progresso.concluidoEm(),
                progresso.atualizadoEm()
        );
    }
}