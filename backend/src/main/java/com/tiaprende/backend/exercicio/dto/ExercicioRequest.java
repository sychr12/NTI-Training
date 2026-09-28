package com.tiaprende.backend.exercicio.dto;

public record ExercicioRequest(
    Long tutorialId,
    String pergunta,
    String alternativaA,
    String alternativaB,
    String alternativaC,
    String alternativaD,
    String respostaCorreta,
    String explicacao
) {
}