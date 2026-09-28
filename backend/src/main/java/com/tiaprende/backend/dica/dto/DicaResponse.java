package com.tiaprende.backend.dica.dto;

import java.time.Instant;

import com.tiaprende.backend.dica.model.Dica;

public record DicaResponse(
        Long id,
        String titulo,
        String conteudo,
        String categoria,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static DicaResponse from(
            Dica dica
    ) {

        return new DicaResponse(
                dica.id(),
                dica.titulo(),
                dica.conteudo(),
                dica.categoria(),
                dica.criadoEm(),
                dica.atualizadoEm()
        );
    }
}