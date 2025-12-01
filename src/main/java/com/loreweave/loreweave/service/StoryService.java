/// ==========================================
/// File Name:    StoryService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for story-related business logic
/// Update History:
///
/// Updated By:   Jamie Coker on 2025-11-30
/// Update Notes: Added getStoryById helper method to fetch a Story
///               by its ID or throw an exception if not found. This
///               is used by StoryPartController when displaying the
///               "not your turn" error page.
/// ==========================================

package com.loreweave.loreweave.service;

import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.repository.StoryRepository;
import org.springframework.stereotype.Service;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Story createStory(Story story) {
        return storyRepository.save(story);
    }

    /**
     * Fetch a Story by its ID or throw if it does not exist.
     *
     * @param id the story ID
     * @return the Story entity
     * @throws IllegalArgumentException if no Story is found
     */
    public Story getStoryById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Story not found with id " + id));
    }
}
