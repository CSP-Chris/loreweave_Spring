/// ==========================================
/// File Name:    StoryPartService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for story part-related business logic
/// Update History:
/// ==========================================

package com.loreweave.loreweave.service;

import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.repository.StoryPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryPartService {

    private final StoryPartRepository storyPartRepository;

    public StoryPartService(StoryPartRepository storyPartRepository) {
        this.storyPartRepository = storyPartRepository;
    }

    public StoryPart addStoryPart(StoryPart storyPart, Character character) throws Exception {
        List<StoryPart> latestParts = storyPartRepository.findLatestStoryPartsForStory(storyPart.getStory().getId());

        if (!latestParts.isEmpty()) {
            StoryPart lastPart = latestParts.getFirst();
            if (lastPart.getContributor().getId().equals(character.getId())) {
                throw new Exception("You cannot add another story part until another user contributes.");
            }
        }

        storyPart.setContributor(character);
        return storyPartRepository.save(storyPart);
    }
}
