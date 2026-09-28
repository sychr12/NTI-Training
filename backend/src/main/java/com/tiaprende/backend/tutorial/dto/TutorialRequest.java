package com.tiaprende.backend.tutorial.dto;

public record TutorialRequest(
    String titulo,
    String descricao,
    String conteudo,
    String categoria,
    String nivel
) {
}
