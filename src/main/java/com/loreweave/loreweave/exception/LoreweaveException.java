package com.loreweave.loreweave.exception;

/// ==========================================
/// File Name:    LoreweaveException.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-22
/// Purpose:      Custom runtime exception used throughout Loreweave.
///               Wraps ErrorCode enum for consistent backend errors.
/// ==========================================

public class LoreweaveException extends RuntimeException {

    private final ErrorCode errorCode;

    public LoreweaveException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LoreweaveException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
