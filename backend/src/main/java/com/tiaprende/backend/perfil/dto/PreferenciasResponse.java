package com.tiaprende.backend.perfil.dto;

public record PreferenciasResponse(
        boolean lembretesEstudo,
        boolean notificacoesEmail
) {
    public static final PreferenciasResponse PADRAO = new PreferenciasResponse(true, true);
}
