package com.tiaprende.backend.auth;

import java.util.List;

public record AdUser(
		String objectGuid,
		String login,
		String displayName,
		String email,
		List<String> memberOf) {
}
