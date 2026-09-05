package com.tiaprende.backend.auth;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nti.auth")
public record AuthProperties(Cors cors, Session session, ActiveDirectory ad) {

	public AuthProperties {
		if (cors == null) {
			cors = new Cors(List.of("http://localhost:3000"));
		}
		if (session == null) {
			session = new Session(28800, 2592000);
		}
		if (ad == null) {
			ad = new ActiveDirectory("", "", "", "", "(sAMAccountName={0})", "");
		}
	}

	public record Cors(List<String> allowedOrigins) {
		public Cors {
			if (allowedOrigins == null || allowedOrigins.isEmpty()) {
				allowedOrigins = List.of("http://localhost:3000");
			}
		}
	}

	public record Session(int defaultTimeoutSeconds, int rememberTimeoutSeconds) {
		public Session {
			if (defaultTimeoutSeconds <= 0) {
				defaultTimeoutSeconds = 28800;
			}
			if (rememberTimeoutSeconds <= 0) {
				rememberTimeoutSeconds = 2592000;
			}
		}
	}

	public record ActiveDirectory(
			String url,
			String baseDn,
			String serviceUserDn,
			String servicePassword,
			String userSearchFilter,
			String requiredGroupDn) {
		public ActiveDirectory {
			url = url == null ? "" : url;
			baseDn = baseDn == null ? "" : baseDn;
			serviceUserDn = serviceUserDn == null ? "" : serviceUserDn;
			servicePassword = servicePassword == null ? "" : servicePassword;
			userSearchFilter = userSearchFilter == null || userSearchFilter.isBlank()
					? "(sAMAccountName={0})"
					: userSearchFilter;
			requiredGroupDn = requiredGroupDn == null ? "" : requiredGroupDn;
		}
	}
}
