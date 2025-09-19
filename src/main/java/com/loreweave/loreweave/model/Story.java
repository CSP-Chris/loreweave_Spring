package com.loreweave.loreweave.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Character creator;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StoryPart> storyParts;

    protected Story() {}

    public Story(String title, Character creator) {
        this.title = title;
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void addStoryPart(StoryPart storyPart) {
        storyParts.add(storyPart);
        storyPart.setStory(this);
    }
}
