package com.loreweave.loreweave.model;

/// ==========================================
/// File Name:    StoryPart.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the StoryPart entity
/// Updated By:   Jamie Coker on 2025-10-12
///Update Notes: Added author relationship (ManyToOne with User)
///            to enable point rewards via LoreVote transactions.
/// ==========================================


import com.loreweave.loreweave.model.Story;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class StoryPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private Character contributor;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int partOrder;

    private LocalDateTime createdAt;

// NEW: link to the author (User who wrote this story part)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "storyPart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LoreVote> loreVotes;

    protected StoryPart() {}

    public StoryPart(Story story, Character contributor, String content, int partOrder) {
        this.story = story;
        this.contributor = contributor;
        this.content = content;
        this.partOrder = partOrder;
        this.createdAt = LocalDateTime.now();
    }

    public void addLoreVote(LoreVote loreVote) {
        loreVotes.add(loreVote);
        loreVote.setStoryPart(this);
    }
}
