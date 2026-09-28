package com.tiaprende.backend.trilha.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tiaprende.backend.trilha.dto.TrilhaRequest;
import com.tiaprende.backend.trilha.dto.TrilhaTutorialResponse;
import com.tiaprende.backend.trilha.model.Trilha;

@Repository
public class TrilhaRepository {

    private final JdbcClient jdbcClient;

    public TrilhaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Trilha> findAll() {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    descricao,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM trilhas
                WHERE ativo = TRUE
                ORDER BY criado_em DESC
                """)
                .query(this::mapTrilha)
                .list();
    }

    public Optional<Trilha> findById(Long id) {

        return jdbcClient.sql("""
                SELECT
                    id,
                    titulo,
                    descricao,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                FROM trilhas
                WHERE id = :id
                  AND ativo = TRUE
                """)
                .param("id", id)
                .query(this::mapTrilha)
                .optional();
    }

    @Transactional
    public Trilha save(TrilhaRequest request) {

        Trilha trilha = jdbcClient.sql("""
                INSERT INTO trilhas (
                    titulo,
                    descricao,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :titulo,
                    :descricao,
                    :nivel,
                    TRUE,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                RETURNING
                    id,
                    titulo,
                    descricao,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("descricao", request.descricao())
                .param("nivel", request.nivel())
                .query(this::mapTrilha)
                .single();

        salvarTutoriais(
                trilha.id(),
                request.tutorialIds()
        );

        return trilha;
    }

    @Transactional
    public Optional<Trilha> update(
            Long id,
            TrilhaRequest request
    ) {

        Optional<Trilha> trilha = jdbcClient.sql("""
                UPDATE trilhas
                SET
                    titulo = :titulo,
                    descricao = :descricao,
                    nivel = :nivel,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE id = :id
                  AND ativo = TRUE
                RETURNING
                    id,
                    titulo,
                    descricao,
                    nivel,
                    ativo,
                    criado_em,
                    atualizado_em
                """)
                .param("titulo", request.titulo())
                .param("descricao", request.descricao())
                .param("nivel", request.nivel())
                .param("id", id)
                .query(this::mapTrilha)
                .optional();

        if (trilha.isPresent()) {

            jdbcClient.sql("""
                    DELETE FROM trilha_tutoriais
                    WHERE trilha_id = :trilhaId
                    """)
                    .param("trilhaId", id)
                    .update();

            salvarTutoriais(
                    id,
                    request.tutorialIds()
            );
        }

        return trilha;
    }

    public List<TrilhaTutorialResponse> findTutoriais(
            Long trilhaId
    ) {

        return jdbcClient.sql("""
                SELECT
                    t.id,
                    t.titulo,
                    t.descricao,
                    t.categoria,
                    t.nivel,
                    tt.ordem
                FROM trilha_tutoriais tt
                INNER JOIN tutoriais t
                    ON t.id = tt.tutorial_id
                WHERE tt.trilha_id = :trilhaId
                  AND t.ativo = TRUE
                ORDER BY tt.ordem
                """)
                .param("trilhaId", trilhaId)
                .query(
                        (rs, rowNum) ->
                                new TrilhaTutorialResponse(
                                        rs.getLong("id"),
                                        rs.getString("titulo"),
                                        rs.getString("descricao"),
                                        rs.getString("categoria"),
                                        rs.getString("nivel"),
                                        rs.getInt("ordem")
                                )
                )
                .list();
    }

    public boolean deactivate(Long id) {

        int rows = jdbcClient.sql("""
                UPDATE trilhas
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

    private void salvarTutoriais(
            Long trilhaId,
            List<Long> tutorialIds
    ) {

        if (tutorialIds == null) {
            return;
        }

        for (int i = 0; i < tutorialIds.size(); i++) {

            jdbcClient.sql("""
                    INSERT INTO trilha_tutoriais (
                        trilha_id,
                        tutorial_id,
                        ordem
                    )
                    VALUES (
                        :trilhaId,
                        :tutorialId,
                        :ordem
                    )
                    """)
                    .param("trilhaId", trilhaId)
                    .param("tutorialId", tutorialIds.get(i))
                    .param("ordem", i + 1)
                    .update();
        }
    }

    private Trilha mapTrilha(
            ResultSet resultSet,
            int rowNumber
    ) throws SQLException {

        Timestamp criadoEm =
                resultSet.getTimestamp("criado_em");

        Timestamp atualizadoEm =
                resultSet.getTimestamp("atualizado_em");

        return new Trilha(
                resultSet.getLong("id"),
                resultSet.getString("titulo"),
                resultSet.getString("descricao"),
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