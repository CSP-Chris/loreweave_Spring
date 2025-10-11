package com.loreweave.loreweave.service;

/*
 ==========================================
 File Name:    TransactionService.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Business logic for processing user transactions.
 ==========================================
 */

import com.loreweave.loreweave.model.Transaction;
import com.loreweave.loreweave.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction tx) {
        // Week 1 placeholder logic — just save
        tx.setStatus("PENDING");
        return transactionRepository.save(tx);
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}

