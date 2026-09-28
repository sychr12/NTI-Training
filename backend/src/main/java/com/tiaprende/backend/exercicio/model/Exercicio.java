package com.tiaprende.backend.exercicio.model;

import java.time.Instant;

public record Exercicio(
    Long id,
    Long tutorialId,
    String pergunta,
    String alternativaA,
    String alternativaB,
    String alternativaC,
    String alternativaD,
    String respostaCorreta,
    String explicacao,
    boolean ativo,
    Instant criadoEm,
    Instant atualizadoEm

) {
}
