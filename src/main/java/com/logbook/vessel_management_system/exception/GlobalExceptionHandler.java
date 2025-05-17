// src/main/java/com/logbook/vessel_management_system/exception/GlobalExceptionHandler.java
package com.logbook.vessel_management_system.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * 
 * Chain of Responsibility Pattern: This class implements a chain of exception handlers,
 * each handling a specific type of exception. The appropriate handler method will be
 * invoked based on the type of exception thrown.
 */

@ControllerAdvice
@Hidden // This excludes the controller advice from OpenAPI documentation
public class GlobalExceptionHandler {

    /**
     * Chain of Responsibility Pattern: Handles ResourceNotFoundException specifically.
     * This handler is part of the chain of exception handlers.
     */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Chain of Responsibility Pattern: Handles IllegalArgumentException specifically.
     * This is another link in the chain of responsibility.
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
         ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
         return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

     /**
     * Chain of Responsibility Pattern: Handles validation exceptions specifically.
     * Another handler in the chain, dedicated to validation errors.
     */

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
         Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                 .collect(Collectors.toMap(
                         fieldError -> fieldError.getField(),
                         fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage()
                 ));

         Map<String, Object> body = new HashMap<>();
         body.put("timestamp", LocalDateTime.now().toString());
         body.put("status", HttpStatus.BAD_REQUEST.value());
         body.put("error", "Validation Failed");
         body.put("errors", fieldErrors);
         body.put("path", request.getDescription(false).replace("uri=", ""));

         return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
     }

     /**
     * Chain of Responsibility Pattern: Acts as the fallback handler for any exceptions
     * not handled by more specific handlers. This is the last link in the chain.
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "An unexpected error occurred: " + ex.getMessage(), request.getDescription(false));
        ex.printStackTrace(); // Good for dev logs
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Simple Error Details class
    public static class ErrorDetails {
        private LocalDateTime timestamp;
        private String message;
        private String details;

        public ErrorDetails(LocalDateTime timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
    }
}