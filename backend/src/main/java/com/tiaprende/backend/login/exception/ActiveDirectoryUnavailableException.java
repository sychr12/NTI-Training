package com.tiaprende.backend.login.exception;
public class ActiveDirectoryUnavailableException extends RuntimeException{
    public ActiveDirectoryUnavailableException(Throwable cause){
        super("Nao foi possivel consultar o Active Directory.", cause);
    }
}