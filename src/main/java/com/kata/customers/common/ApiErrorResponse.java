package com.kata.customers.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    String timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors
) {
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return new ApiErrorResponse(
            OffsetDateTime.now().toString(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path,
            null
        );
    }

    public static ApiErrorResponse ofValidation(
        HttpStatus status,
        String message,
        String path,
        Map<String, String> validationErrors
    ) {
        return new ApiErrorResponse(
            OffsetDateTime.now().toString(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path,
            validationErrors
        );
    }
}
