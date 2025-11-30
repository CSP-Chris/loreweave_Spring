package com.loreweave.loreweave.service;

/// ==========================================
/// File Name:    TransactionService.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-10
/// Purpose:      Simplified helper for financial logic if needed.
/// Updated By:   Jamie Coker on 2025-10-12
/// Update Notes: Deprecated direct transaction creation;
///               all transactions now occur through LoreVote.
/// Updated By:   Jamie Coker on 2025-11-30
/// Update Notes: Added standardized error messages and optional
///               recordVoteTransaction helper to support Week 3
///               transaction QA. Added logging hooks for debugging
///               vote → transaction flow without reintroducing
///               legacy transaction logic.
/// ==========================================

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    // Standardized error codes for logging (matches LV001–LV005 scheme)
    public static final String ERR_DUPLICATE      = "LV001_DUPLICATE_VOTE";
    public static final String ERR_NOT_FOUND      = "LV002_STORY_PART_NOT_FOUND";
    public static final String ERR_SELF_VOTE      = "LV003_SELF_VOTE";
    public static final String ERR_INVALID_TYPE   = "LV004_INVALID_VOTE_TYPE";
    public static final String ERR_TRANSACTION    = "LV005_TRANSACTION_FAILURE";

    /**
     * Optional helper used for logging or analytics.
     * Called from VoteController after a successful LoreVote save.
     * Does NOT create a separate transaction row.
     */
    public void recordVoteTransaction(Long voterId, Long receiverId, int amount, String status) {
        log.info("Transaction recorded: voter={}, receiver={}, amount={}, status={}",
                voterId, receiverId, amount, status);
    }

    /**
     * Log failures for QA debugging.
     */
    public void logTransactionFailure(String errorCode, String details) {
        log.error("Transaction failure [{}]: {}", errorCode, details);
    }
}