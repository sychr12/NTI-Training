package com.tiaprende.backend.trilha.dto;

import java.util.List;

public record TrilhaRequest(
        String titulo,
        String descricao,
        String nivel,
        List<Long> tutorialIds
) {
}