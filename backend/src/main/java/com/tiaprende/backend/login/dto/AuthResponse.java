package com.tiaprende.backend.login.dto;

import com.tiaprende.backend.login.session.AuthSession.SessionUser;

public record AuthResponse(
    SessionUser usuario
) {
}