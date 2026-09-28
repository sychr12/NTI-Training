package com.tiaprende.backend.tutorial.dto;

import java.time.Instant;
import com.tiaprende.backend.tutorial.model.Tutorial;

public record TutorialResponse(
    Long id,
    String titulo,
    String descricao,
    String conteudo,
    String categoria,
    String nivel,
    boolean ativo,
    Instant criadoEm,
    Instant atualizadoEm
) {

    public static TutorialResponse from (Tutorial tutorial){
        return new TutorialResponse(tutorial.id(), tutorial.titulo(), tutorial.descrisao(), tutorial.conteudo(), tutorial.categoria(), tutorial.nivel(), tutorial.ativo(), tutorial.criadoEm(), tutorial.atulizadoEm());
    }
}
