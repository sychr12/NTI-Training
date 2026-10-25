package com.tiaprende.backend.favorito.dto;

import java.time.Instant;

public record FavoritoResponse(
        Long tutorialId,
        Instant criadoEm
) {
}
