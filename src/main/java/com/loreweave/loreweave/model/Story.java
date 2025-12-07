package com.loreweave.loreweave.model;

/// ==========================================
/// File Name:    Story.java
/// Created By:   Chris Ennis
/// Created On:   2025-09-15
/// Purpose:      JPA file that creates the Story entity
/// Update History: Jamie Coker on 2025-12-07
/// /// Update Notes:   Added dynamic storyPart count for each story displayed on the /stories page.
/// ///                 Added @Transient partCount mapping and controller logic to populate story thread counts.
///
/// ==========================================



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
    @Transient
    private int partCount;

    public int getPartCount() { return partCount; }
    public void setPartCount(int partCount) { this.partCount = partCount; }

}
