package com.tiaprende.backend.exercicio.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.exercicio.dto.ExercicioRequest;
import com.tiaprende.backend.exercicio.model.Exercicio;

@Repository
public class ExercicioRepository {

    private final JdbcClient jdbcClient;

    public ExercicioRepository(
            JdbcClient jdbcClient
    ) {
        this.jdbcClient = jdbcClient;
    }

    public List<Exercicio> findByTutorial(
            Long tutorialId
    ) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    tutorial_id,
                    pergunta,
                    alternativa_a,
                    alternativa_b,
                    alternativa_c,
                    alternativa_d,
                    resposta_correta,
                    explicacao,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM exercicios
                WHERE tutorial_id = :tutorialId
                  AND ativo = TRUE
                  AND tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                ORDER BY id
                """)
                .param("tutorialId", tutorialId)
                .query(this::mapExercicio)
                .list();
    }

    public Optional<Exercicio> findById(Long id) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    tutorial_id,
                    pergunta,
                    alternativa_a,
                    alternativa_b,
                    alternativa_c,
                    alternativa_d,
                    resposta_correta,
                    explicacao,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM exercicios
                WHERE id = :id
                  AND ativo = TRUE
                  AND tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                """)
                .param("id", id)
                .query(this::mapExercicio)
                .optional();
    }

    public Exercicio save(
            ExercicioRequest request
    ) {

        return jdbcClient.sql("""
                INSERT INTO exercicios (
                    tutorial_id,
                    pergunta,
                    alternativa_a,
                    alternativa_b,
                    alternativa_c,
                    alternativa_d,
                    resposta_correta,
                    explicacao,
                    ativo,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :tutorialId,
                    :pergunta,
                    :alternativaA,
                    :alternativaB,
                    :alternativaC,
                    :alternativaD,
                    :respostaCorreta,
                    :explicacao,
                    TRUE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                RETURNING
                    id,
                    tutorial_id,
                    pergunta,
                    alternativa_a,
                    alternativa_b,
                    alternativa_c,
                    alternativa_d,
                    resposta_correta,
                    explicacao,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param(
                        "tutorialId",
                        request.tutorialId()
                )
                .param(
                        "pergunta",
                        request.pergunta()
                )
                .param(
                        "alternativaA",
                        request.alternativaA()
                )
                .param(
                        "alternativaB",
                        request.alternativaB()
                )
                .param(
                        "alternativaC",
                        request.alternativaC()
                )
                .param(
                        "alternativaD",
                        request.alternativaD()
                )
                .param(
                        "respostaCorreta",
                        request.respostaCorreta().trim()
                                .toUpperCase(java.util.Locale.ROOT)
                )
                .param(
                        "explicacao",
                        request.explicacao()
                )
                .query(this::mapExercicio)
                .single();
    }

    public Optional<Exercicio> update(
            Long id,
            ExercicioRequest request
    ) {

        return jdbcClient.sql("""
                UPDATE exercicios
                SET
                    tutorial_id = :tutorialId,
                    pergunta = :pergunta,
                    alternativa_a = :alternativaA,
                    alternativa_b = :alternativaB,
                    alternativa_c = :alternativaC,
                    alternativa_d = :alternativaD,
                    resposta_correta = :respostaCorreta,
                    explicacao = :explicacao,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                  AND tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                RETURNING
                    id,
                    tutorial_id,
                    pergunta,
                    alternativa_a,
                    alternativa_b,
                    alternativa_c,
                    alternativa_d,
                    resposta_correta,
                    explicacao,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("id", id)
                .param("tutorialId", request.tutorialId())
                .param("pergunta", request.pergunta())
                .param("alternativaA", request.alternativaA())
                .param("alternativaB", request.alternativaB())
                .param("alternativaC", request.alternativaC())
                .param("alternativaD", request.alternativaD())
                .param(
                        "respostaCorreta",
                        request.respostaCorreta().trim().toUpperCase(java.util.Locale.ROOT)
                )
                .param("explicacao", request.explicacao())
                .query(this::mapExercicio)
                .optional();
    }

    public boolean deactivate(Long id) {

        int rows = jdbcClient.sql("""
                UPDATE exercicios
                SET
                    ativo = FALSE,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                  AND tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                """)
                .param("id", id)
                .update();

        return rows > 0;
    }

    public void salvarResposta(Long usuarioId, Long exercicioId, String resposta, boolean correto) {
        jdbcClient.sql("""
                INSERT INTO respostas_exercicios (usuario_id, exercicio_id, resposta, correto)
                VALUES (:usuarioId, :exercicioId, :resposta, :correto)
                ON CONFLICT (usuario_id, exercicio_id) DO UPDATE SET
                    resposta = EXCLUDED.resposta,
                    correto = EXCLUDED.correto,
                    atualizado_em = CURRENT_TIMESTAMP
                """).param("usuarioId", usuarioId).param("exercicioId", exercicioId)
                .param("resposta", resposta).param("correto", correto).update();
    }

    private Exercicio mapExercicio(
            ResultSet rs,
            int rowNumber
    ) throws SQLException {

        Timestamp criadoEm =
                rs.getTimestamp("criado_em");

        Timestamp atualizadoEm =
                rs.getTimestamp("atualizado_em");

        return new Exercicio(
                rs.getLong("id"),
                rs.getLong("tutorial_id"),
                rs.getString("pergunta"),
                rs.getString("alternativa_a"),
                rs.getString("alternativa_b"),
                rs.getString("alternativa_c"),
                rs.getString("alternativa_d"),
                rs.getString("resposta_correta"),
                rs.getString("explicacao"),
                rs.getBoolean("ativo"),
                criadoEm == null
                        ? null
                        : criadoEm.toInstant(),
                atualizadoEm == null
                        ? null
                        : atualizadoEm.toInstant()
        );
    }
}