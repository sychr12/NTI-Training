package com.tiaprende.backend.login.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(){
        super("Login ou senha invalidos");
    }
}