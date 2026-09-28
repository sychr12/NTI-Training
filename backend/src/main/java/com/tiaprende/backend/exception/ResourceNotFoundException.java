package com.tiaprende.backend.exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(
            String message
    ) {
        super(message);
    }
}