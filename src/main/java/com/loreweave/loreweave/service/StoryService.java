/// ==========================================
/// File Name:    StoryService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for story-related business logic
/// Update History:
/// ==========================================

package com.loreweave.loreweave.service;

import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.repository.StoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
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
}
