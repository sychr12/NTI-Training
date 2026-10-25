package com.tiaprende.backend.perfil.dto;

/*
 * Campos nulos mantem o valor atual, entao o frontend pode enviar so o que mudou.
 */
public record AtualizarPreferenciasRequest(
        Boolean lembretesEstudo,
        Boolean notificacoesEmail
) {
}
