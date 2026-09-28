package com.tiaprende.backend.user.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.login.dto.AdUser;
import com.tiaprende.backend.user.model.AppUser;

@Repository 
public class UserRepository {
    private final JdbcClient jdbcClient;
    public UserRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public AppUser upsertFromAd(AdUser adUser){

        return jdbcClient.sql("""
                INSERT INTO usuarios(
                ad_object_guid,
                login,
                nome,
                email,
                ultimo_login
                )
                VALUES (:adObjectGuid, :login, :nome, :email, CURRENT_TIMESTAMP)
                ON CONFLICT (ad_object_guid)
                DO UPDATE SET
                    login = EXCLUDED.login,
                    nome = EXCLUDED.nome,
                    email = EXCLUDED.email,
                    ultimo_login = CURRENT_TIMESTAMP
                RETURNING
                    id,
                    ad_object_guid,
                    login,
                    nome,
                    email,
                    perfil,
                    ativo,
                    ultimo_login
                """)
                .param("adObjectGuid", adUser.objectGuid())
                .param("login", adUser.login())
                .param("nome", adUser.displayName())
                .param ("email", adUser.email())
                .query(this::mapUser)
                .single();

    }

    public Optional<AppUser> findById(Long id){
        return jdbcClient.sql("""
            SELECT
                id,
                ad_object_guid,
                login,
                nome,
                email,
                perfil,
                ativo,
                ultimo_login
                FROM usuarios
                WHERE id = :id
        """)
        .param("id", id)
        .query(this::mapUser)
        .optional();
    }

    public Optional <AppUser> findByLogin(String login){
        return jdbcClient.sql("""
                SELECT
                    id,
                    ad_object_guid,
                    login,
                    nome,
                    email,
                    perfil,
                    ativo,
                    ultimo_login
                FROM usuarios
                WHERE login = :login
                """)
                .param("login", login)
                .query(this::mapUser)
                .optional();

    }

    public List<AppUser> findAll(){
        return jdbcClient.sql("""
            SELECT
                id,
                ad_object_guid,
                login,
                nome,
                email,
                perfil,
                ativo,
                ultimo_login
            FROM usuarios
            ORDER BY nome
        """)
        .query(this::mapUser)
        .list();
    }

    public Optional <AppUser> update(
        Long id,
        String perfil,
        boolean ativo
    ){
        return jdbcClient.sql("""
            UPDATE usuarios
            SET
                perfil = :perfil,
                ativo = :ativo
            WHERE id = :id
            RETURNING
                id,
                ad_object_guid,
                login,
                nome,
                email,
                perfil,
                ativo,
                ultimo_login
        """)
        .param("perfil", perfil)
        .param("ativo", ativo)
        .param("id", id)
        .query(this::mapUser)
        .optional();
    }
    private AppUser mapUser(
        ResultSet resultSet,
        int rowNumber
    ) throws SQLException{
        Timestamp ultimoLogin = resultSet.getTimestamp("ultimo_login");
        return new AppUser(
            resultSet.getLong("id"),
                resultSet.getString("ad_object_guid"),
                resultSet.getString("login"),
                resultSet.getString("nome"),
                resultSet.getString("email"),
                resultSet.getString("perfil"),
                resultSet.getBoolean("ativo"),
                ultimoLogin == null
                        ? null
                        : ultimoLogin.toInstant()
        );
    }
    
}
