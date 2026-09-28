package com.tiaprende.backend.tutorial.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.tutorial.dto.TutorialRequest;
import com.tiaprende.backend.tutorial.model.Tutorial;

@Repository
public class TutorialRepository {

    private final JdbcClient jdbcClient;

    public TutorialRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Tutorial save(TutorialRequest request) {

        return jdbcClient.sql("""
                INSERT INTO tutoriais (
                    titulo,
                    descricao,
                    conteudo,
                    categoria,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :titulo,
                    :descricao,
                    :conteudo,
                    :categoria,
                    :nivel,
                    TRUE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                RETURNING
                    id,
                    titulo,
                    descricao,
                    conteudo,
                    categoria,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("descricao", request.descricao())
                .param("conteudo", request.conteudo())
                .param("categoria", request.categoria())
                .param("nivel", request.nivel())
                .query(this::mapTutorial)
                .single();
    }

    public List<Tutorial> findAll() {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    descricao,
                    conteudo,
                    categoria,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM tutoriais
                WHERE ativo = TRUE
                ORDER BY criado_em DESC
                """)
                .query(this::mapTutorial)
                .list();
    }

    public Optional<Tutorial> findById(Long id) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    descricao,
                    conteudo,
                    categoria,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM tutoriais
                WHERE id = :id
                  AND ativo = TRUE
                """)
                .param("id", id)
                .query(this::mapTutorial)
                .optional();
    }

    public Optional<Tutorial> update(
            Long id,
            TutorialRequest request
    ) {

        return jdbcClient.sql("""
                UPDATE tutoriais
                SET
                    titulo = :titulo,
                    descricao = :descricao,
                    conteudo = :conteudo,
                    categoria = :categoria,
                    nivel = :nivel,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                RETURNING
                    id,
                    titulo,
                    descricao,
                    conteudo,
                    categoria,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("descricao", request.descricao())
                .param("conteudo", request.conteudo())
                .param("categoria", request.categoria())
                .param("nivel", request.nivel())
                .param("id", id)
                .query(this::mapTutorial)
                .optional();
    }

    public boolean deactivate(Long id) {

        int rows = jdbcClient.sql("""
                UPDATE tutoriais
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

    private Tutorial mapTutorial(
            ResultSet resultSet,
            int rowNumber
    ) throws SQLException {

        Timestamp criadoEm =
                resultSet.getTimestamp("criado_em");

        Timestamp atualizadoEm =
                resultSet.getTimestamp("atualizado_em");

        return new Tutorial(
                resultSet.getLong("id"),
                resultSet.getString("titulo"),
                resultSet.getString("descricao"),
                resultSet.getString("conteudo"),
                resultSet.getString("categoria"),
                resultSet.getString("nivel"),
                resultSet.getBoolean("ativo"),

                criadoEm == null
                        ? null
                        : criadoEm.toInstant(),

                atualizadoEm == null
                        ? null
                        : atualizadoEm.toInstant()
        );
    }
}