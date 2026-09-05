package com.tiaprende.backend.auth;

import java.io.Serializable;

public final class AuthSession {

	public static final String USER = "NTI_AUTH_USER";

	private AuthSession() {
	}

	public record SessionUser(
			Long id,
			String login,
			String nome,
			String email,
			String perfil) implements Serializable {
	}
}
