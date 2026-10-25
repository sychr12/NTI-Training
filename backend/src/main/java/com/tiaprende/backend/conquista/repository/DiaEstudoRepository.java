package com.tiaprende.backend.conquista.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class DiaEstudoRepository {

    private final JdbcClient jdbcClient;

    public DiaEstudoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /*
     * Marca o dia de hoje (data do banco) como dia de estudo do usuario. Idempotente.
     */
    public void registrarHoje(Long usuarioId) {

        int jaRegistrado = jdbcClient.sql("""
                SELECT COUNT(*) FROM dias_estudo
                WHERE usuario_id = :usuarioId AND dia = CURRENT_DATE
                """)
                .param("usuarioId", usuarioId)
                .query(Integer.class)
                .single();

        if (jaRegistrado > 0) {
            return;
        }

        try {
            jdbcClient.sql("INSERT INTO dias_estudo (usuario_id, dia) VALUES (:usuarioId, CURRENT_DATE)")
                    .param("usuarioId", usuarioId)
                    .update();
        }
        catch (DuplicateKeyException ignored) {
            // Outra requisicao registrou o mesmo dia ao mesmo tempo.
        }
    }

    /*
     * Dias com atividade, do mais recente para o mais antigo. Inclui datas derivadas do progresso
     * e das respostas, para que o historico anterior a esta tabela tambem conte.
     */
    public List<LocalDate> findDias(Long usuarioId) {

        return jdbcClient.sql("""
                SELECT dia FROM (
                    SELECT dia FROM dias_estudo WHERE usuario_id = :usuarioId
                    UNION
                    SELECT CAST(iniciado_em AS DATE) FROM progresso_tutoriais WHERE usuario_id = :usuarioId
                    UNION
                    SELECT CAST(concluido_em AS DATE) FROM progresso_tutoriais WHERE usuario_id = :usuarioId AND concluido_em IS NOT NULL
                    UNION
                    SELECT CAST(atualizado_em AS DATE) FROM respostas_exercicios WHERE usuario_id = :usuarioId
                ) dias
                ORDER BY dia DESC
                """)
                .param("usuarioId", usuarioId)
                .query(LocalDate.class)
                .list();
    }

    public LocalDate hoje() {
        return jdbcClient.sql("SELECT CURRENT_DATE").query(LocalDate.class).single();
    }
}
