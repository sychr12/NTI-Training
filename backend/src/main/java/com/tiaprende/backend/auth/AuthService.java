package com.tiaprende.backend.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiaprende.backend.auth.AuthenticationExceptions.UnauthorizedGroupException;
import com.tiaprende.backend.user.AppUser;
import com.tiaprende.backend.user.UserRepository;

@Service
public class AuthService {

	private final ActiveDirectoryClient activeDirectoryClient;
	private final UserRepository userRepository;

	public AuthService(ActiveDirectoryClient activeDirectoryClient, UserRepository userRepository) {
		this.activeDirectoryClient = activeDirectoryClient;
		this.userRepository = userRepository;
	}

	@Transactional
	public AppUser login(String username, String password) {
		AdUser adUser = activeDirectoryClient.authenticate(username, password);
		AppUser user = userRepository.upsertFromAd(adUser);

		if (!user.ativo()) {
			throw new UnauthorizedGroupException();
		}

		return user;
	}
}
