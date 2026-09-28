package com.tiaprende.backend.glossario.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.glossario.dto.TermoGlossarioRequest;
import com.tiaprende.backend.glossario.model.TermoGlossario;

@Repository
public class GlossarioRepository {

    private final JdbcClient jdbcClient;

    public GlossarioRepository(
            JdbcClient jdbcClient
    ) {
        this.jdbcClient = jdbcClient;
    }

    public List<TermoGlossario> findAll() {

        return jdbcClient.sql("""
                SELECT
                    id,
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM glossario
                WHERE ativo = TRUE
                ORDER BY termo
                """)
                .query(this::mapTermo)
                .list();
    }

    public Optional<TermoGlossario> findById(
            Long id
    ) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM glossario
                WHERE id = :id
                  AND ativo = TRUE
                """)
                .param("id", id)
                .query(this::mapTermo)
                .optional();
    }

    public List<TermoGlossario> search(
            String busca
    ) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM glossario
                WHERE ativo = TRUE
                  AND (
                    LOWER(termo)
                        LIKE LOWER(:busca)
                    OR
                    LOWER(definicao)
                        LIKE LOWER(:busca)
                    OR
                    LOWER(categoria)
                        LIKE LOWER(:busca)
                  )
                ORDER BY termo
                """)
                .param(
                        "busca",
                        "%" + busca + "%"
                )
                .query(this::mapTermo)
                .list();
    }

    public TermoGlossario save(
            TermoGlossarioRequest request
    ) {

        return jdbcClient.sql("""
                INSERT INTO glossario (
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :termo,
                    :definicao,
                    :categoria,
                    TRUE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                RETURNING
                    id,
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("termo", request.termo())
                .param(
                        "definicao",
                        request.definicao()
                )
                .param(
                        "categoria",
                        request.categoria()
                )
                .query(this::mapTermo)
                .single();
    }

    public Optional<TermoGlossario> update(
            Long id,
            TermoGlossarioRequest request
    ) {

        return jdbcClient.sql("""
                UPDATE glossario
                SET
                    termo = :termo,
                    definicao = :definicao,
                    categoria = :categoria,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                RETURNING
                    id,
                    termo,
                    definicao,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("termo", request.termo())
                .param(
                        "definicao",
                        request.definicao()
                )
                .param(
                        "categoria",
                        request.categoria()
                )
                .param("id", id)
                .query(this::mapTermo)
                .optional();
    }

    public boolean deactivate(Long id) {

        int rows = jdbcClient.sql("""
                UPDATE glossario
                SET
                    ativo = FALSE,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                """)
                .param("id", id)
                .update();

        return rows > 0;
    }

    private TermoGlossario mapTermo(
            ResultSet rs,
            int rowNumber
    ) throws SQLException {

        Timestamp criadoEm =
                rs.getTimestamp("criado_em");

        Timestamp atualizadoEm =
                rs.getTimestamp("atualizado_em");

        return new TermoGlossario(
                rs.getLong("id"),
                rs.getString("termo"),
                rs.getString("definicao"),
                rs.getString("categoria"),
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