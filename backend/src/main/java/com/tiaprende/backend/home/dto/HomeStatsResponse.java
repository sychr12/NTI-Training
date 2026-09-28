package com.tiaprende.backend.home.dto;

public record HomeStatsResponse(
    int cursosEmAndamento,
    int cursosConcluido,
    int exerciciosConcluidos,
    int progressoGeral
) {
} 

