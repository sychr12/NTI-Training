package com.tiaprende.backend.perfil.repository;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.perfil.dto.PreferenciasResponse;

@Repository
public class PreferenciasRepository {

    private final JdbcClient jdbcClient;

    public PreferenciasRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<PreferenciasResponse> findByUsuario(Long usuarioId) {

        return jdbcClient.sql("""
                SELECT lembretes_estudo, notificacoes_email
                FROM preferencias_usuarios
                WHERE usuario_id = :usuarioId
                """)
                .param("usuarioId", usuarioId)
                .query((resultSet, rowNumber) -> new PreferenciasResponse(
                        resultSet.getBoolean("lembretes_estudo"),
                        resultSet.getBoolean("notificacoes_email")))
                .optional();
    }

    /*
     * Atualiza ou cria o registro do usuario. Feito em dois passos (em vez de ON CONFLICT)
     * para funcionar tambem no H2 dos testes.
     */
    public void save(Long usuarioId, PreferenciasResponse preferencias) {

        int updated = jdbcClient.sql("""
                UPDATE preferencias_usuarios
                SET lembretes_estudo = :lembretes,
                    notificacoes_email = :email,
                    atualizado_em = CURRENT_TIMESTAMP
                WHERE usuario_id = :usuarioId
                """)
                .param("usuarioId", usuarioId)
                .param("lembretes", preferencias.lembretesEstudo())
                .param("email", preferencias.notificacoesEmail())
                .update();

        if (updated == 0) {
            jdbcClient.sql("""
                    INSERT INTO preferencias_usuarios (usuario_id, lembretes_estudo, notificacoes_email)
                    VALUES (:usuarioId, :lembretes, :email)
                    """)
                    .param("usuarioId", usuarioId)
                    .param("lembretes", preferencias.lembretesEstudo())
                    .param("email", preferencias.notificacoesEmail())
                    .update();
        }
    }
}
