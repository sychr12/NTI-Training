package com.tiaprende.backend.dica.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.dica.dto.DicaRequest;
import com.tiaprende.backend.dica.model.Dica;

@Repository
public class DicaRepository {

    private final JdbcClient jdbcClient;

    public DicaRepository(
            JdbcClient jdbcClient
    ) {
        this.jdbcClient = jdbcClient;
    }

    public List<Dica> findAll() {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM dicas
                WHERE ativo = TRUE
                ORDER BY criado_em DESC
                """)
                .query(this::mapDica)
                .list();
    }

    public Optional<Dica> findById(Long id) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM dicas
                WHERE id = :id
                  AND ativo = TRUE
                """)
                .param("id", id)
                .query(this::mapDica)
                .optional();
    }

    public List<Dica> findByCategoria(
            String categoria
    ) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM dicas
                WHERE ativo = TRUE
                  AND UPPER(categoria) = UPPER(:categoria)
                ORDER BY criado_em DESC
                """)
                .param("categoria", categoria)
                .query(this::mapDica)
                .list();
    }

    public Dica save(
            DicaRequest request
    ) {

        return jdbcClient.sql("""
                INSERT INTO dicas (
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :titulo,
                    :conteudo,
                    :categoria,
                    TRUE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                RETURNING
                    id,
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("conteudo", request.conteudo())
                .param("categoria", request.categoria())
                .query(this::mapDica)
                .single();
    }

    public Optional<Dica> update(
            Long id,
            DicaRequest request
    ) {

        return jdbcClient.sql("""
                UPDATE dicas
                SET
                    titulo = :titulo,
                    conteudo = :conteudo,
                    categoria = :categoria,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                RETURNING
                    id,
                    titulo,
                    conteudo,
                    categoria,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("conteudo", request.conteudo())
                .param("categoria", request.categoria())
                .param("id", id)
                .query(this::mapDica)
                .optional();
    }

    public boolean deactivate(Long id) {

        int rows = jdbcClient.sql("""
                UPDATE dicas
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

    private Dica mapDica(
            ResultSet rs,
            int rowNumber
    ) throws SQLException {

        Timestamp criadoEm =
                rs.getTimestamp("criado_em");

        Timestamp atualizadoEm =
                rs.getTimestamp("atualizado_em");

        return new Dica(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("conteudo"),
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