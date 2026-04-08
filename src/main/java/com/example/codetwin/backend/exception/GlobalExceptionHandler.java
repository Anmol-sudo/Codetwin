package com.example.codetwin.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.codetwin.backend.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Validation errors (DTO validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        "Validation failed",
                        errors
                )
        );
    }

    // ✅ Runtime errors (like duplicate email)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );
    }
}