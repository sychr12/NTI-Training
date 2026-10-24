package com.tiaprende.backend.login.ldap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tiaprende.backend.login.dto.AdUser;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;

/**
 * Autenticador local para desenvolvimento fora da rede do AD. Ativo somente no perfil "dev".
 */
@Component
@Profile("dev")
public class DevAuthenticator implements DirectoryAuthenticator {

	private static final Logger log = LoggerFactory.getLogger(DevAuthenticator.class);
	private static final String DEV_OBJECT_GUID = "00000000-0000-0000-0000-00000000de00";

	private final String username;
	private final String password;
	private final String nome;
	private final String email;

	public DevAuthenticator(
			@Value("${nti.auth.dev.username}") String username,
			@Value("${nti.auth.dev.password}") String password,
			@Value("${nti.auth.dev.nome}") String nome,
			@Value("${nti.auth.dev.email}") String email) {
		this.username = username;
		this.password = password;
		this.nome = nome;
		this.email = email;
		log.warn("Perfil 'dev' ativo: login pelo Active Directory DESATIVADO. Usuario local: {}", username);
	}

	@Override
	public AdUser authenticate(String username, String password) {
		if (!StringUtils.hasText(username) || !StringUtils.hasText(password)
				|| !this.username.equalsIgnoreCase(username)
				|| !MessageDigest.isEqual(
						this.password.getBytes(StandardCharsets.UTF_8),
						password.getBytes(StandardCharsets.UTF_8))) {
			throw new InvalidCredentialsException();
		}
		return new AdUser(DEV_OBJECT_GUID, this.username, nome, email, List.of());
	}
}
