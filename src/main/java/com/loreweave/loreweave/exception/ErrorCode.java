package com.loreweave.loreweave.exception;

/// ==========================================
/// File Name:    ErrorCode.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-22
/// Purpose:      Centralized enumeration for backend error codes used in
///               the voting → transaction pipeline. Supports consistent
///               messaging, logging, and UI alerts in Milestone 2 Week 3.
/// ==========================================

public enum ErrorCode {
    LV001_DUPLICATE_VOTE,        // User already voted on this story part
    LV002_INVALID_STORY_PART,    // Story part not found or no longer exists
    LV003_SELF_VOTE_NOT_ALLOWED, // User attempted to vote on their own content
    LV004_INVALID_VOTE_TYPE,     // Null or unsupported vote type
    LV005_TRANSACTION_FAILURE    // DB or server error during vote processing
}
