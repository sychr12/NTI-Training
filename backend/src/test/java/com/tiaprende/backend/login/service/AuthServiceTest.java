package com.tiaprende.backend.login.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.tiaprende.backend.login.config.AuthProperties;
import com.tiaprende.backend.login.dto.AdUser;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;
import com.tiaprende.backend.login.exception.TooManyAttemptsException;
import com.tiaprende.backend.login.exception.UnauthorizedGroupException;
import com.tiaprende.backend.login.ldap.DirectoryAuthenticator;
import com.tiaprende.backend.user.model.AppUser;
import com.tiaprende.backend.user.repository.UserRepository;

class AuthServiceTest {

	private final DirectoryAuthenticator directory = mock(DirectoryAuthenticator.class);
	private final UserRepository repository = mock(UserRepository.class);
	private final LoginAttemptLimiter limiter = new LoginAttemptLimiter(new AuthProperties(null, null, null, null), new MutableClock());
	private final AuthService service = new AuthService(directory, repository, limiter);

	private final AdUser adUser = new AdUser("guid-1", "maria", "Maria", "maria@empresa.com", List.of());

	@Test
	void loginValidoGravaOUsuario() {
		when(directory.authenticate("maria", "certa")).thenReturn(adUser);
		when(repository.upsertFromAd(adUser)).thenReturn(appUser(true));

		assertThat(service.login("maria", "certa", "10.0.0.1").login()).isEqualTo("maria");
	}

	@Test
	void senhaErradaRepetidaBloqueiaSemConsultarOAd() {
		when(directory.authenticate("maria", "errada")).thenThrow(new InvalidCredentialsException());
		for (int i = 0; i < 5; i++) {
			assertThatThrownBy(() -> service.login("maria", "errada", "10.0.0.1"))
					.isInstanceOf(InvalidCredentialsException.class);
		}

		assertThatThrownBy(() -> service.login("maria", "certa", "10.0.0.1"))
				.isInstanceOf(TooManyAttemptsException.class);
		verify(directory, never()).authenticate("maria", "certa");
	}

	@Test
	void usuarioInativoNoBancoNaoEntra() {
		when(directory.authenticate(any(), any())).thenReturn(adUser);
		when(repository.upsertFromAd(adUser)).thenReturn(appUser(false));

		assertThatThrownBy(() -> service.login("maria", "certa", "10.0.0.1"))
				.isInstanceOf(UnauthorizedGroupException.class);
	}

	private AppUser appUser(boolean ativo) {
		return new AppUser(1L, "guid-1", "maria", "Maria", "maria@empresa.com", "ALUNO", ativo, null);
	}
}
