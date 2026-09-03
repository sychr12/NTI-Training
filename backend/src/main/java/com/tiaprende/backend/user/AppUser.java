package com.tiaprende.backend.user;

import java.time.Instant;

public record AppUser(
		Long id,
		String adObjectGuid,
		String login,
		String nome,
		String email,
		String perfil,
		boolean ativo,
		Instant ultimoLogin) {
}
