package com.tiaprende.backend.login.exception;
public class UnauthorizedGroupException extends RuntimeException{
    public UnauthorizedGroupException(){
        super("Usuario sem permissao para acessar o NTI Training.");
    }
}