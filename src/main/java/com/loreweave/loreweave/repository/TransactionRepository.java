package com.loreweave.loreweave.repository;

/*
 ==========================================
 File Name:    TransactionRepository.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Handles database operations for user transactions.
 ==========================================
 */

import com.loreweave.loreweave.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
