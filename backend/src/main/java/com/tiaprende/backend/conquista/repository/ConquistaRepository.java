package com.tiaprende.backend.conquista.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ConquistaRepository {

    private final JdbcClient jdbcClient;

    public ConquistaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public record Registro(Instant conquistadaEm, boolean vista) {
    }

    /*
     * Numeros usados para calcular XP e medalhas, sempre considerando so conteudo ativo.
     */
    public record Totais(int tutoriaisConcluidos, int categoriasConcluidas, int acertos) {
    }

    public Totais totais(Long usuarioId) {

        return jdbcClient.sql("""
                SELECT
                    (SELECT COUNT(*) FROM progresso_tutoriais p
                       JOIN tutoriais t ON t.id = p.tutorial_id AND t.ativo = TRUE
                      WHERE p.usuario_id = :usuarioId AND p.status = 'CONCLUIDO') AS concluidos,
                    (SELECT COUNT(DISTINCT COALESCE(t.categoria, '')) FROM progresso_tutoriais p
                       JOIN tutoriais t ON t.id = p.tutorial_id AND t.ativo = TRUE
                      WHERE p.usuario_id = :usuarioId AND p.status = 'CONCLUIDO') AS categorias,
                    (SELECT COUNT(*) FROM respostas_exercicios r
                       JOIN exercicios e ON e.id = r.exercicio_id AND e.ativo = TRUE
                       JOIN tutoriais t ON t.id = e.tutorial_id AND t.ativo = TRUE
                      WHERE r.usuario_id = :usuarioId AND r.correto = TRUE) AS acertos
                """)
                .param("usuarioId", usuarioId)
                .query((rs, row) -> new Totais(rs.getInt("concluidos"), rs.getInt("categorias"), rs.getInt("acertos")))
                .single();
    }

    public Map<String, Registro> findByUsuario(Long usuarioId) {

        Map<String, Registro> registros = new HashMap<>();
        jdbcClient.sql("""
                SELECT codigo, conquistada_em, vista
                FROM conquistas_usuarios
                WHERE usuario_id = :usuarioId
                """)
                .param("usuarioId", usuarioId)
                .query((rs, row) -> {
                    Timestamp conquistadaEm = rs.getTimestamp("conquistada_em");
                    registros.put(rs.getString("codigo"),
                            new Registro(conquistadaEm == null ? null : conquistadaEm.toInstant(), rs.getBoolean("vista")));
                    return null;
                })
                .list();
        return registros;
    }

    public void registrar(Long usuarioId, String codigo) {
        try {
            jdbcClient.sql("INSERT INTO conquistas_usuarios (usuario_id, codigo) VALUES (:usuarioId, :codigo)")
                    .param("usuarioId", usuarioId)
                    .param("codigo", codigo)
                    .update();
        }
        catch (DuplicateKeyException ignored) {
            // Ja registrada por outra requisicao.
        }
    }

    public void marcarVistas(Long usuarioId) {
        jdbcClient.sql("UPDATE conquistas_usuarios SET vista = TRUE WHERE usuario_id = :usuarioId AND vista = FALSE")
                .param("usuarioId", usuarioId)
                .update();
    }
}
