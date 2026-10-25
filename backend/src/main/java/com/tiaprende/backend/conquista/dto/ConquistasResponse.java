package com.tiaprende.backend.conquista.dto;

import java.time.Instant;
import java.util.List;

public record ConquistasResponse(
        int xp,
        Nivel nivel,
        Sequencia sequencia,
        List<Medalha> medalhas
) {

    /*
     * xpProximo e nulo no ultimo nivel.
     */
    public record Nivel(
            int numero,
            String nome,
            int xpInicio,
            Integer xpProximo
    ) {
    }

    public record Sequencia(
            int atual,
            int melhor,
            boolean estudouHoje
    ) {
    }

    /*
     * nova = conquistada e ainda nao exibida ao usuario (ver POST /api/conquistas/vistas).
     */
    public record Medalha(
            String codigo,
            String titulo,
            String descricao,
            String icone,
            int progresso,
            int meta,
            boolean conquistada,
            Instant conquistadaEm,
            boolean nova
    ) {
    }
}
