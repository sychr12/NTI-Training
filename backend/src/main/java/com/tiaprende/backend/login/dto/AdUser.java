package com.tiaprende.backend.login.dto;

import java.util.List;

public record AdUser(
		String objectGuid,
		String login,
		String displayName,
		String email,
		List<String> memberOf) {
}
