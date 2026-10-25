package com.tiaprende.backend.exercicio.dto;

/*
 * Resposta que o proprio usuario ja deu a um exercicio. Inclui o gabarito,
 * que ele ja viu ao responder, para o quiz mostrar a correcao ao voltar ao tutorial.
 */
public record MinhaRespostaResponse(
    Long exercicioId,
    String resposta,
    boolean correto,
    String respostaCorreta,
    String explicacao
) {
}
