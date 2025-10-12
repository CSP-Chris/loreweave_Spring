/// ==========================================
/// File Name:    LoreVote.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the LoreVote entity
/// Updated By:   Jamie Coker on 2025-10-12
/// Update Notes: Integrated transaction data (amount, status, receiverId)
///           to combine voting and transactions in one entity.
/// ==========================================

package com.loreweave.loreweave.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class LoreVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private StoryPart storyPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    private LocalDateTime createdAt;
// NEW: transaction-related fields added for integration
    @Column(nullable = false)
    private double amount = 0.0; // value assigned to the vote

    @Column(name = "receiver_id")
    private Long receiverId; // who gets the reward (usually story author)

    @Column(name = "status")
    private String status = "COMPLETED"; // SUCCESS / FAILED / PENDING
// Now each LoreVote doubles as a transaction record.


    protected LoreVote() {}

    public LoreVote(StoryPart storyPart, User voter, VoteType voteType) {
        this.storyPart = storyPart;
        this.voter = voter;
        this.voteType = voteType;
        this.createdAt = LocalDateTime.now();
    }

    public enum VoteType {
        POSITIVE, NEGATIVE
    }
}