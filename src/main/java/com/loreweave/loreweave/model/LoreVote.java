/// ==========================================
/// File Name:    LoreVote.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the LoreVote entity
/// Update History:
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