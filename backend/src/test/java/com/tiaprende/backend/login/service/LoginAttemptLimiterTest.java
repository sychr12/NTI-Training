package com.tiaprende.backend.login.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.tiaprende.backend.login.config.AuthProperties;
import com.tiaprende.backend.login.exception.TooManyAttemptsException;

class LoginAttemptLimiterTest {

	private final MutableClock clock = new MutableClock();
	// Padrão: 5 tentativas por usuário em 15 minutos.
	private final LoginAttemptLimiter limiter = new LoginAttemptLimiter(new AuthProperties(null, null, null, null), clock);

	@Test
	void bloqueiaUsuarioDepoisDoLimiteDeFalhas() {
		for (int i = 0; i < 5; i++) {
			limiter.checkAllowed("maria", "10.0.0.1");
			limiter.recordFailure("maria", "10.0.0.1");
		}

		assertThatThrownBy(() -> limiter.checkAllowed("maria", "10.0.0.1"))
				.isInstanceOf(TooManyAttemptsException.class)
				.hasMessageContaining("15 minutos");
	}

	@Test
	void ignoraMaiusculasEEspacosNoUsuario() {
		for (int i = 0; i < 5; i++) {
			limiter.recordFailure(" Maria ", "10.0.0.1");
		}

		assertThatThrownBy(() -> limiter.checkAllowed("maria", "10.0.0.2"))
				.isInstanceOf(TooManyAttemptsException.class);
	}

	@Test
	void liberaDepoisQueAJanelaExpira() {
		for (int i = 0; i < 5; i++) {
			limiter.recordFailure("maria", "10.0.0.1");
		}
		clock.advance(Duration.ofMinutes(15));

		assertThatCode(() -> limiter.checkAllowed("maria", "10.0.0.1")).doesNotThrowAnyException();
	}

	@Test
	void loginComSucessoZeraAsFalhasDoUsuario() {
		for (int i = 0; i < 4; i++) {
			limiter.recordFailure("maria", "10.0.0.1");
		}
		limiter.recordSuccess("maria");
		limiter.recordFailure("maria", "10.0.0.1");

		assertThatCode(() -> limiter.checkAllowed("maria", "10.0.0.1")).doesNotThrowAnyException();
	}

	@Test
	void bloqueiaIpQueTentaMuitosUsuariosDiferentes() {
		for (int i = 0; i < 20; i++) {
			limiter.recordFailure("usuario" + i, "10.0.0.9");
		}

		assertThatThrownBy(() -> limiter.checkAllowed("outro", "10.0.0.9"))
				.isInstanceOf(TooManyAttemptsException.class);
		assertThatCode(() -> limiter.checkAllowed("outro", "10.0.0.10")).doesNotThrowAnyException();
	}
}
