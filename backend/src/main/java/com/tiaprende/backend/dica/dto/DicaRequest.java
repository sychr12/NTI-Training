package com.tiaprende.backend.dica.dto;

public record DicaRequest(
        String titulo,
        String conteudo,
        String categoria
) {
}