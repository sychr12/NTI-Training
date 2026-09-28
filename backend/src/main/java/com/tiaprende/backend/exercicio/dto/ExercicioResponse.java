package com.tiaprende.backend.exercicio.dto;

import com.tiaprende.backend.exercicio.model.Exercicio;

public record ExercicioResponse(
    Long id,
    Long tutorialId,
    String pergunta,
    String alternativaA,
    String alternativaB,
    String alternativaC,
    String alternativaD
) {
    public static ExercicioResponse from(
        Exercicio exercicio
    ){
        return new ExercicioResponse(exercicio.id(), exercicio.tutorialId(), exercicio.pergunta(), exercicio.alternativaA(), exercicio.alternativaB(), exercicio.alternativaC(), exercicio.alternativaD());
    }
}
