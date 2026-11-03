package com.tiaprende.backend.exercicio.dto;

/*
 * Resposta do proprio usuario a um exercicio. O gabarito e a explicacao so saem do servidor
 * quando o exercicio esta encerrado (acertou ou gastou as tentativas), para nao aparecerem nem na rede.
 */
public record MinhaRespostaResponse(
    Long exercicioId,
    String resposta,
    boolean correto,
    int tentativas,
    boolean encerrada,
    String respostaCorreta,
    String explicacao
) {

    public static final int MAX_TENTATIVAS = 2;

    public static MinhaRespostaResponse de(
            Long exercicioId,
            String resposta,
            boolean correto,
            int tentativas,
            String respostaCorreta,
            String explicacao
    ) {
        boolean encerrada = correto || tentativas >= MAX_TENTATIVAS;
        return new MinhaRespostaResponse(
                exercicioId,
                resposta,
                correto,
                tentativas,
                encerrada,
                encerrada ? respostaCorreta : null,
                encerrada ? explicacao : null
        );
    }
}
