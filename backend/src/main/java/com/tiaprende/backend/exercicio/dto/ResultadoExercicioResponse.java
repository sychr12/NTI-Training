package com.tiaprende.backend.exercicio.dto;

public record ResultadoExercicioResponse(
    boolean correto,
    String respostaCorreta,
    String explicacao
) {
}
