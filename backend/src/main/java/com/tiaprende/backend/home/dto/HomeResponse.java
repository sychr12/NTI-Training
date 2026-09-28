package com.tiaprende.backend.home.dto;

import java.util.List;

public record HomeResponse(
    String nomeUsuario,
    HomeStatsResponse estatisticas,
    List<ContinueLearningResponse> continuarAprendendo
) {
}
