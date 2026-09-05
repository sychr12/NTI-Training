package com.tiaprende.backend.auth;

final class AuthenticationExceptions {

	private AuthenticationExceptions() {
	}

	static class AuthConfigurationException extends RuntimeException {
		AuthConfigurationException(String message) {
			super(message);
		}
	}

	static class InvalidCredentialsException extends RuntimeException {
		InvalidCredentialsException() {
			super("Login ou senha invalidos.");
		}
	}

	static class UnauthorizedGroupException extends RuntimeException {
		UnauthorizedGroupException() {
			super("Usuario sem permissao para acessar o NTI Training.");
		}
	}

	static class ActiveDirectoryUnavailableException extends RuntimeException {
		ActiveDirectoryUnavailableException(Throwable cause) {
			super("Nao foi possivel consultar o Active Directory.", cause);
		}
	}
}
