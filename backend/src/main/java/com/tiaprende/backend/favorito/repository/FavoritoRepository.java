package com.tiaprende.backend.favorito.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.favorito.dto.FavoritoResponse;

@Repository
public class FavoritoRepository {

    private final JdbcClient jdbcClient;

    public FavoritoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<FavoritoResponse> findByUsuario(Long usuarioId) {

        return jdbcClient.sql("""
                SELECT tutorial_id, criado_em
                FROM favoritos
                WHERE tutorial_id IN (SELECT id FROM tutoriais WHERE ativo = TRUE)
                  AND usuario_id = :usuarioId
                ORDER BY criado_em DESC
                """)
                .param("usuarioId", usuarioId)
                .query((resultSet, rowNumber) -> {
                    Timestamp criadoEm = resultSet.getTimestamp("criado_em");
                    return new FavoritoResponse(
                            resultSet.getLong("tutorial_id"),
                            criadoEm == null ? null : criadoEm.toInstant());
                })
                .list();
    }

    /*
     * Idempotente: favoritar duas vezes nao gera erro nem registro duplicado.
     * Confere antes de inserir (em vez de ON CONFLICT) para funcionar tambem no H2 dos testes;
     * a chave primaria ainda impede duplicatas em requisicoes simultaneas.
     */
    public void add(Long usuarioId, Long tutorialId) {

        boolean exists = jdbcClient.sql("""
                SELECT COUNT(*)
                FROM favoritos
                WHERE usuario_id = :usuarioId
                  AND tutorial_id = :tutorialId
                """)
                .param("usuarioId", usuarioId)
                .param("tutorialId", tutorialId)
                .query(Long.class)
                .single() > 0;

        if (exists) {
            return;
        }

        try {
            jdbcClient.sql("""
                    INSERT INTO favoritos (usuario_id, tutorial_id)
                    VALUES (:usuarioId, :tutorialId)
                    """)
                    .param("usuarioId", usuarioId)
                    .param("tutorialId", tutorialId)
                    .update();
        }
        catch (DuplicateKeyException ignored) {
            // Outra requisicao favoritou o mesmo tutorial ao mesmo tempo: o resultado e o mesmo.
        }
    }

    public void remove(Long usuarioId, Long tutorialId) {

        jdbcClient.sql("""
                DELETE FROM favoritos
                WHERE usuario_id = :usuarioId
                  AND tutorial_id = :tutorialId
                """)
                .param("usuarioId", usuarioId)
                .param("tutorialId", tutorialId)
                .update();
    }
}
