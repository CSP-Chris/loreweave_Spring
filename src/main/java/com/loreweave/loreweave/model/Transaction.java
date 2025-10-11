package com.loreweave.loreweave.model;

/*
 ==========================================
 File Name:    Transaction.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Represents a user-to-user transaction for point/credit transfers.
 ==========================================
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lore_transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private Double amount;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String status; // PENDING, SUCCESS, FAILED

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
