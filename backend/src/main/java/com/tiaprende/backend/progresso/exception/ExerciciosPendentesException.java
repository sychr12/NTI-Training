package com.tiaprende.backend.progresso.exception;

/*
 * Tentativa de concluir um tutorial sem responder todos os exercicios dele (HTTP 409).
 */
public class ExerciciosPendentesException extends RuntimeException {

    public ExerciciosPendentesException(int pendentes) {
        super(pendentes == 1
                ? "Responda o exercicio deste tutorial antes de concluir."
                : "Responda os " + pendentes + " exercicios deste tutorial antes de concluir.");
    }
}
