package com.tiaprende.backend.exercicio.dto;

import com.tiaprende.backend.exercicio.model.Exercicio;

/**
 * Exercício completo, com a resposta correta e a explicação.
 * Só é exposto para quem edita conteúdo (PROFESSOR, SUPERVISOR e ADMIN).
 */
public record ExercicioGabaritoResponse(
    Long id,
    Long tutorialId,
    String pergunta,
    String alternativaA,
    String alternativaB,
    String alternativaC,
    String alternativaD,
    String respostaCorreta,
    String explicacao
) {
    public static ExercicioGabaritoResponse from(
        Exercicio exercicio
    ){
        return new ExercicioGabaritoResponse(exercicio.id(), exercicio.tutorialId(), exercicio.pergunta(), exercicio.alternativaA(), exercicio.alternativaB(), exercicio.alternativaC(), exercicio.alternativaD(), exercicio.respostaCorreta(), exercicio.explicacao());
    }
}
