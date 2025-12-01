package com.loreweave.loreweave.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/// ==========================================
/// File Name:    GlobalExceptionHandler.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-22
/// Purpose:      Centralized exception handler for Loreweave.
///               Converts backend exceptions into consistent JSON
///               structures for REST endpoints and logs all errors.
/// ==========================================

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles custom Loreweave exceptions.
     */
    @ExceptionHandler(LoreweaveException.class)
    public ResponseEntity<ErrorResponse> handleLoreweaveException(LoreweaveException ex) {
        log.error("LoreweaveException [{}]: {}", ex.getErrorCode(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getErrorCode().name(),
                        ex.getMessage()
                ));
    }

    /**
     * Handles all uncaught errors and avoids backend crashes.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "SERVER_ERROR",
                        "An unexpected error occurred."
                ));
    }
}
