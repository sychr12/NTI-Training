package com.tiaprende.backend.glossario.dto;

import java.time.Instant;

import com.tiaprende.backend.glossario.model.TermoGlossario;

public record TermoGlossarioResponse(
        Long id,
        String termo,
        String definicao,
        String categoria,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static TermoGlossarioResponse from(
            TermoGlossario termo
    ) {

        return new TermoGlossarioResponse(
                termo.id(),
                termo.termo(),
                termo.definicao(),
                termo.categoria(),
                termo.criadoEm(),
                termo.atualizadoEm()
        );
    }
}