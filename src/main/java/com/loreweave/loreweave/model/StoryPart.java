package com.loreweave.loreweave.model;

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
