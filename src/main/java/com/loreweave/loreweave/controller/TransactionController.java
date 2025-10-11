package com.loreweave.loreweave.controller;

/*
 ==========================================
 File Name:    TransactionController.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      REST endpoints for creating and viewing transactions.
 ==========================================
 */

import com.loreweave.loreweave.model.Transaction;
import com.loreweave.loreweave.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction tx) {
        return transactionService.createTransaction(tx);
    }

    @GetMapping("/history/{userId}")
    public List<Transaction> getHistory(@PathVariable Long userId) {
        return transactionService.getUserTransactions(userId);
    }
}
