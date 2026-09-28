package com.tiaprende.backend.exception;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tiaprende.backend.login.exception.ActiveDirectoryUnavailableException;
import com.tiaprende.backend.login.exception.AuthConfigurationException;
import com.tiaprende.backend.login.exception.InvalidCredentialsException;
import com.tiaprende.backend.login.exception.UnauthorizedGroupException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleStatus(
            org.springframework.web.server.ResponseStatusException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason(), request);
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Requisicao invalida.", request);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP nao permitido.", request);
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de conteudo nao suportado.", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(UnauthorizedGroupException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedGroup(
            UnauthorizedGroupException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(ActiveDirectoryUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleActiveDirectoryUnavailable(
            ActiveDirectoryUnavailableException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(AuthConfigurationException.class)
    public ResponseEntity<ErrorResponse> handleAuthConfiguration(
            AuthConfigurationException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Nao foi possivel realizar a operacao devido a um conflito nos dados.",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        org.slf4j.LoggerFactory.getLogger(getClass()).error("Erro ao processar {}", request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.",
                request);
    }

    @ExceptionHandler({ResourceNotFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class,
            org.springframework.web.servlet.NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            Exception exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI(),
                Instant.now());

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}