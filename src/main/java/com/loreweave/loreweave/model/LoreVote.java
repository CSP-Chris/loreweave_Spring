package com.loreweave.loreweave.model;
/// ==========================================
/// File Name:    LoreVote.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the LoreVote entity
/// Updated By:   Jamie Coker on 2025-10-12
/// Update Notes: Integrated transaction data (amount, status, receiverId)
///           to combine voting and transactions in one entity.
/// Updated By:   Jamie Coker on 2025-11-30
/// Update Notes: Added validation annotations, strengthened default
///               transaction state, and confirmed unique constraint for
///               Week 3 Milestone error-handling and QA.
/// ==========================================



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "lore_vote",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"story_part_id", "voter_id"}
        )
)
public class LoreVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private StoryPart storyPart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // NEW: transaction-related fields
    @NotNull
    @Column(nullable = false)
    private double amount = 0.0;  // +1 or -1 for votes

    @Column(name = "receiver_id")
    private Long receiverId;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status = "COMPLETED"; // SUCCESS / FAILED / PENDING


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