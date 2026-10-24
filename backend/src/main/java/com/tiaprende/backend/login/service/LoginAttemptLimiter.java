package com.tiaprende.backend.login.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tiaprende.backend.login.config.AuthProperties;
import com.tiaprende.backend.login.exception.TooManyAttemptsException;

/**
 * Conta tentativas de login com senha errada, por usuário e por IP, em janelas fixas de tempo.
 * Bloqueia antes que o AD bloqueie a conta pela política de senha (em memória: vale por instância).
 */
@Component
public class LoginAttemptLimiter {

	// O IP pode ser compartilhado (NAT da rede interna), então tem um limite maior que o do usuário.
	private static final int IP_MULTIPLIER = 4;
	private static final int PRUNE_THRESHOLD = 10_000;

	private final int maxAttempts;
	private final Duration window;
	private final Clock clock;
	private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

	@Autowired
	public LoginAttemptLimiter(AuthProperties properties) {
		this(properties, Clock.systemUTC());
	}

	LoginAttemptLimiter(AuthProperties properties, Clock clock) {
		this.maxAttempts = properties.login().maxAttempts();
		this.window = Duration.ofMinutes(properties.login().blockMinutes());
		this.clock = clock;
	}

	public void checkAllowed(String username, String ip) {
		Instant now = clock.instant();
		ensureBelowLimit(userKey(username), maxAttempts, now);
		ensureBelowLimit(ipKey(ip), maxAttempts * IP_MULTIPLIER, now);
	}

	public void recordFailure(String username, String ip) {
		Instant now = clock.instant();
		increment(userKey(username), now);
		increment(ipKey(ip), now);
		if (attempts.size() > PRUNE_THRESHOLD) {
			attempts.entrySet().removeIf((entry) -> entry.getValue().expired(now, window));
		}
	}

	public void recordSuccess(String username) {
		attempts.remove(userKey(username));
	}

	private void ensureBelowLimit(String key, int limit, Instant now) {
		Attempts current = attempts.get(key);
		if (current != null && !current.expired(now, window) && current.count() >= limit) {
			Duration remaining = Duration.between(now, current.windowStart().plus(window));
			throw new TooManyAttemptsException(Math.max(1, (remaining.toSeconds() + 59) / 60));
		}
	}

	private void increment(String key, Instant now) {
		attempts.compute(key, (k, current) -> current == null || current.expired(now, window)
				? new Attempts(1, now)
				: new Attempts(current.count() + 1, current.windowStart()));
	}

	private static String userKey(String username) {
		return "user:" + (username == null ? "" : username.trim().toLowerCase(Locale.ROOT));
	}

	private static String ipKey(String ip) {
		return "ip:" + (ip == null ? "" : ip);
	}

	private record Attempts(int count, Instant windowStart) {
		boolean expired(Instant now, Duration window) {
			return !now.isBefore(windowStart.plus(window));
		}
	}
}
