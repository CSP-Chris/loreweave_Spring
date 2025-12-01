package com.loreweave.loreweave.exception;

/// ==========================================
/// File Name:    ErrorResponse.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-22
/// Purpose:      JSON DTO for REST error responses. Not used for UI
///               redirects, but useful for API endpoints and logging.
/// ==========================================

public record ErrorResponse(
        String errorCode,
        String message
) {}
