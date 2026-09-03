package com.tiaprende.backend.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tiaprende.backend.auth.AdUser;

@Repository
public class UserRepository {

	private final JdbcClient jdbcClient;

	public UserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public AppUser upsertFromAd(AdUser adUser) {
		return jdbcClient.sql("""
				INSERT INTO usuarios (ad_object_guid, login, nome, email, ultimo_login)
				VALUES (:adObjectGuid, :login, :nome, :email, CURRENT_TIMESTAMP)
				ON CONFLICT (ad_object_guid)
				DO UPDATE SET
				    login = EXCLUDED.login,
				    nome = EXCLUDED.nome,
				    email = EXCLUDED.email,
				    ultimo_login = CURRENT_TIMESTAMP
				RETURNING id, ad_object_guid, login, nome, email, perfil, ativo, ultimo_login
				""")
				.param("adObjectGuid", adUser.objectGuid())
				.param("login", adUser.login())
				.param("nome", adUser.displayName())
				.param("email", adUser.email())
				.query(this::mapUser)
				.single();
	}

	private AppUser mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
		Timestamp ultimoLogin = resultSet.getTimestamp("ultimo_login");
		return new AppUser(
				resultSet.getLong("id"),
				resultSet.getString("ad_object_guid"),
				resultSet.getString("login"),
				resultSet.getString("nome"),
				resultSet.getString("email"),
				resultSet.getString("perfil"),
				resultSet.getBoolean("ativo"),
				ultimoLogin == null ? null : ultimoLogin.toInstant());
	}
}
