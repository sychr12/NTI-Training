package com.tiaprende.backend.login.service;

import com.tiaprende.backend.login.ldap.DirectoryAuthenticator;
import com.tiaprende.backend.user.model.AppUser;
import org.springframework.stereotype.Service;

import com.tiaprende.backend.login.exception.InvalidCredentialsException;
import com.tiaprende.backend.login.exception.UnauthorizedGroupException;
import com.tiaprende.backend.login.dto.AdUser;
import com.tiaprende.backend.user.repository.UserRepository;

@Service
public class AuthService {

	private final DirectoryAuthenticator directoryAuthenticator;
	private final UserRepository userRepository;
	private final LoginAttemptLimiter attemptLimiter;

	public AuthService(DirectoryAuthenticator directoryAuthenticator, UserRepository userRepository,
			LoginAttemptLimiter attemptLimiter) {
		this.directoryAuthenticator = directoryAuthenticator;
		this.userRepository = userRepository;
		this.attemptLimiter = attemptLimiter;
	}

	// Sem @Transactional: não segura conexão do banco enquanto espera o AD; o upsert é um único comando.
	public AppUser login(String username, String password, String ip) {
		attemptLimiter.checkAllowed(username, ip);

		AdUser adUser;
		try {
			adUser = directoryAuthenticator.authenticate(username, password);
		}
		catch (InvalidCredentialsException ex) {
			attemptLimiter.recordFailure(username, ip);
			throw ex;
		}
		attemptLimiter.recordSuccess(username);

		AppUser user = userRepository.upsertFromAd(adUser);
		if (!user.ativo()) {
			throw new UnauthorizedGroupException();
		}
		return user;
	}
}
