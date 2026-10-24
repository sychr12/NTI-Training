package com.tiaprende.backend.login.session;

import java.io.Serializable;

import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

public final class AuthSession {

	public static final String USER = "NTI_AUTH_USER";

	/**
	 * Atributo de requisição que faz o Spring Session gravar o cookie como persistente ("lembrar de mim").
	 * O Spring Boot já configura o DefaultCookieSerializer para reconhecer este atributo.
	 */
	public static final String REMEMBER_ME = SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR;

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
