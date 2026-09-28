package com.tiaprende.backend.progresso.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.progresso.dto.ProgressoTrilhaResponse;
import com.tiaprende.backend.progresso.model.ProgressoTutorial;

@Repository
public class ProgressoRepository {

    private final JdbcClient jdbcClient;

    public ProgressoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<ProgressoTutorial> findByUsuarioAndTutorial(
            Long usuarioId,
            Long tutorialId) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    usuario_id,
                    tutorial_id,
                    status,
                    iniciado_em,
                    concluido_em,
                    atualizado_em
                FROM progresso_tutoriais
                WHERE tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                  AND usuario_id = :usuarioId
                  AND tutorial_id = :tutorialId
                """)
                .param("usuarioId", usuarioId)
                .param("tutorialId", tutorialId)
                .query(this::mapProgresso)
                .optional();
    }

    public List<ProgressoTutorial> findByUsuario(
            Long usuarioId) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    usuario_id,
                    tutorial_id,
                    status,
                    iniciado_em,
                    concluido_em,
                    atualizado_em
                FROM progresso_tutoriais
                WHERE tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                  AND usuario_id = :usuarioId
                ORDER BY atualizado_em DESC
                """)
                .param("usuarioId", usuarioId)
                .query(this::mapProgresso)
                .list();
    }

    public ProgressoTutorial saveOrUpdate(
            Long usuarioId,
            Long tutorialId,
            String status) {

        return jdbcClient.sql("""
                INSERT INTO progresso_tutoriais (
                    usuario_id,
                    tutorial_id,
                    status,
                    iniciado_em,
                    concluido_em,
                    atualizado_em
                )
                VALUES (
                    :usuarioId,
                    :tutorialId,
                    :status,
                    CURRENT_TIMESTAMP,
                    CASE
                        WHEN :status = 'CONCLUIDO'
                        THEN CURRENT_TIMESTAMP
                        ELSE NULL
                    END,
                    CURRENT_TIMESTAMP
                )

                ON CONFLICT (usuario_id, tutorial_id)

                DO UPDATE SET

                    status = EXCLUDED.status,

                    iniciado_em =
                        COALESCE(
                            progresso_tutoriais.iniciado_em,
                            CURRENT_TIMESTAMP
                        ),

                    concluido_em =
                        CASE
                            WHEN EXCLUDED.status = 'CONCLUIDO'
                            THEN COALESCE(
                                progresso_tutoriais.concluido_em,
                                CURRENT_TIMESTAMP
                            )
                            ELSE NULL
                        END,

                    atualizado_em = CURRENT_TIMESTAMP

                RETURNING
                    id,
                    usuario_id,
                    tutorial_id,
                    status,
                    iniciado_em,
                    concluido_em,
                    atualizado_em
                """)
                .param("usuarioId", usuarioId)
                .param("tutorialId", tutorialId)
                .param("status", status)
                .query(this::mapProgresso)
                .single();
    }

    public List<ProgressoTrilhaResponse> findProgressoTrilhas(
            Long usuarioId) {

        return jdbcClient.sql("""
                SELECT
                    tr.id AS trilha_id,
                    tr.titulo,

                    COUNT(t.id) AS total_tutoriais,

                    COUNT(
                        CASE
                            WHEN pt.status = 'CONCLUIDO'
                            THEN 1
                        END
                    ) AS concluidos

                FROM trilhas tr

                LEFT JOIN trilha_tutoriais tt
                    ON tt.trilha_id = tr.id

                LEFT JOIN tutoriais t ON t.id = tt.tutorial_id AND t.ativo = TRUE

                LEFT JOIN progresso_tutoriais pt
                    ON pt.tutorial_id = t.id
                    AND pt.usuario_id = :usuarioId

                WHERE tr.ativo = TRUE

                GROUP BY
                    tr.id,
                    tr.titulo

                ORDER BY tr.titulo
                """)
                .param("usuarioId", usuarioId)
                .query((rs, rowNum) -> {

                    int total = rs.getInt("total_tutoriais");

                    int concluidos = rs.getInt("concluidos");

                    int percentual = total == 0
                            ? 0
                            : (concluidos * 100) / total;

                    return new ProgressoTrilhaResponse(
                            rs.getLong("trilha_id"),
                            rs.getString("titulo"),
                            total,
                            concluidos,
                            percentual);
                })
                .list();
    }

    private ProgressoTutorial mapProgresso(
            ResultSet resultSet,
            int rowNumber) throws SQLException {

        Timestamp iniciadoEm = resultSet.getTimestamp("iniciado_em");

        Timestamp concluidoEm = resultSet.getTimestamp("concluido_em");

        Timestamp atualizadoEm = resultSet.getTimestamp("atualizado_em");

        return new ProgressoTutorial(
                resultSet.getLong("id"),
                resultSet.getLong("usuario_id"),
                resultSet.getLong("tutorial_id"),
                resultSet.getString("status"),

                iniciadoEm == null
                        ? null
                        : iniciadoEm.toInstant(),

                concluidoEm == null
                        ? null
                        : concluidoEm.toInstant(),

                atualizadoEm == null
                        ? null
                        : atualizadoEm.toInstant());
    }

    public int countTutoriaisIniciados(
            Long usuarioId) {
        return jdbcClient.sql("""
                    SELECT COUNT(*)
                    FROM progresso_tutoriais
                    WHERE tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                  AND usuario_id = :usuarioId
                """)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();
    }

    public int countTutoriaisConcluidos(
            Long usuarioId) {
        return jdbcClient.sql("""
                SELECT COUNT(*)
                FROM progresso_tutoriais
                WHERE tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                  AND usuario_id = :usuarioId
                    AND status = 'CONCLUIDO'
                """)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();
    }

    public int countTrilhasConcluidas(
            Long usuarioId) {

        return jdbcClient.sql("""
                SELECT COUNT(*)
                FROM (
                    SELECT
                        tr.id
                    FROM trilhas tr

                    LEFT JOIN trilha_tutoriais tt
                        ON tt.trilha_id = tr.id

                LEFT JOIN tutoriais t ON t.id = tt.tutorial_id AND t.ativo = TRUE

                    LEFT JOIN progresso_tutoriais pt
                        ON pt.tutorial_id = t.id
                        AND pt.usuario_id = :usuarioId
                        AND pt.status = 'CONCLUIDO'

                    WHERE tr.ativo = TRUE

                    GROUP BY tr.id

                    HAVING COUNT(t.id) > 0
                       AND COUNT(t.id)
                           = COUNT(pt.tutorial_id)
                ) trilhas_concluidas
                """)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();
    }

    public int calcularProgressoGeral(
            Long usuarioId) {

        return jdbcClient.sql("""
                SELECT
                    CASE
                        WHEN COUNT(t.id) = 0
                            THEN 0
                        ELSE
                            (
                                COUNT(
                                    CASE
                                        WHEN pt.status = 'CONCLUIDO'
                                        THEN 1
                                    END
                                ) * 100
                            ) / COUNT(t.id)
                    END
                FROM tutoriais t

                LEFT JOIN progresso_tutoriais pt
                    ON pt.tutorial_id = t.id
                    AND pt.usuario_id = :usuarioId

                WHERE t.ativo = TRUE
                """)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();
    }
}