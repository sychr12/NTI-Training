package com.tiaprende.backend.trilha.dto;

import java.time.Instant;
import java.util.List;

import com.tiaprende.backend.trilha.model.Trilha;

public record TrilhaResponse(
        Long id,
        String titulo,
        String descricao,
        String nivel,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm,
        List<TrilhaTutorialResponse> tutoriais
) {

    public static TrilhaResponse from(
            Trilha trilha,
            List<TrilhaTutorialResponse> tutoriais
    ) {

        return new TrilhaResponse(
                trilha.id(),
                trilha.titulo(),
                trilha.descricao(),
                trilha.nivel(),
                trilha.ativo(),
                trilha.criadoEm(),
                trilha.atualizadoEm(),
                tutoriais
        );
    }
}