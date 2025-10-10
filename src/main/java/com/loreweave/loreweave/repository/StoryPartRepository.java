/// ==========================================
/// File Name:    StoryPartRepository.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      JPA repository for the StoryPart entity
/// Update History:
/// ==========================================

package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.StoryPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoryPartRepository extends JpaRepository<StoryPart, Long> {
    @Query("SELECT sp FROM StoryPart sp WHERE sp.story.id = :storyId ORDER BY sp.createdAt DESC")
    List<StoryPart> findLatestStoryPartsForStory(Long storyId);
}
