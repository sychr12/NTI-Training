package com.tiaprende.backend.login.exception;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException(long minutes) {
        super("Muitas tentativas de login. Tente novamente em " + minutes + (minutes == 1 ? " minuto." : " minutos."));
    }
}
