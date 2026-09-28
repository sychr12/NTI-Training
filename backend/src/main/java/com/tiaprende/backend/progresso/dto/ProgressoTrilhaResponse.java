package com.tiaprende.backend.progresso.dto;

public record ProgressoTrilhaResponse(
        Long trilhaId,
        String titulo,
        int totalTutoriais,
        int tutoriaisConcluidos,
        int percentual
) {
}