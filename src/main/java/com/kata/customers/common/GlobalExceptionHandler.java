package com.kata.customers.common;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .badRequest()
            .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
        BadCredentialsException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiErrorResponse.of(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales invalidas",
                    request.getRequestURI()
                )
            );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex
            .getBindingResult()
            .getFieldErrors()
            .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
            .badRequest()
            .body(
                ApiErrorResponse.ofValidation(
                    HttpStatus.BAD_REQUEST,
                    "Error de validacion en la solicitud",
                    request.getRequestURI(),
                    validationErrors
                )
            );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
        HttpMessageNotReadableException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .badRequest()
            .body(
                ApiErrorResponse.of(
                    HttpStatus.BAD_REQUEST,
                    "JSON invalido o mal formado",
                    request.getRequestURI()
                )
            );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest request
    ) {
        String message = "Parametro invalido: " + ex.getName();
        return ResponseEntity
            .badRequest()
            .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, message, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
        Exception ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiErrorResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor",
                    request.getRequestURI()
                )
            );
    }
}
