package com.att.tdp.popcorn_palace.exception;

import org.springframework.http.*;
import java.util.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
//global exception Handler for better error handling and documentation
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 (Validation Errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((FieldError error) -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.BAD_REQUEST.value(), "error", "Invalid input data", "details", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 404 (Resource Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.NOT_FOUND.value(), "error", "Resource not found", "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 409 (Duplicate or Conflict)
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<Map<String, Object>> handleDataConflictException(DataConflictException ex) {
        Map<String, Object> response = Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.CONFLICT.value(), "error", "Conflict", "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 500 (Unexpected Issues)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = Map.of("timestamp", LocalDateTime.now(), "status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "Internal Server Error", "message", "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
