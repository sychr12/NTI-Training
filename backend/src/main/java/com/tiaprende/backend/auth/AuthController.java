package com.tiaprende.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tiaprende.backend.auth.AuthSession.SessionUser;
import com.tiaprende.backend.auth.AuthenticationExceptions.ActiveDirectoryUnavailableException;
import com.tiaprende.backend.auth.AuthenticationExceptions.AuthConfigurationException;
import com.tiaprende.backend.auth.AuthenticationExceptions.InvalidCredentialsException;
import com.tiaprende.backend.auth.AuthenticationExceptions.UnauthorizedGroupException;
import com.tiaprende.backend.user.AppUser;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final AuthProperties properties;

	public AuthController(AuthService authService, AuthProperties properties) {
		this.authService = authService;
		this.properties = properties;
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		try {
			AppUser user = authService.login(loginRequest.usuario(), loginRequest.senha());
			HttpSession oldSession = request.getSession(false);
			if (oldSession != null) {
				oldSession.invalidate();
			}

			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(sessionTimeout(loginRequest.lembrar()));

			SessionUser sessionUser = new SessionUser(
					user.id(),
					user.login(),
					user.nome(),
					user.email(),
					user.perfil());
			session.setAttribute(AuthSession.USER, sessionUser);

			return new AuthResponse(sessionUser);
		}
		catch (InvalidCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
		}
		catch (UnauthorizedGroupException ex) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
		}
		catch (AuthConfigurationException | ActiveDirectoryUnavailableException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
		}
	}

	@GetMapping("/me")
	public AuthResponse me(HttpServletRequest request) {
		SessionUser user = currentUser(request);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessao expirada ou inexistente.");
		}
		return new AuthResponse(user);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.noContent().build();
	}

	private SessionUser currentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		Object user = session.getAttribute(AuthSession.USER);
		return user instanceof SessionUser sessionUser ? sessionUser : null;
	}

	private int sessionTimeout(boolean remember) {
		AuthProperties.Session session = properties.session();
		return remember ? session.rememberTimeoutSeconds() : session.defaultTimeoutSeconds();
	}

	public record LoginRequest(String usuario, String senha, boolean lembrar) {
	}

	public record AuthResponse(SessionUser usuario) {
	}
}
