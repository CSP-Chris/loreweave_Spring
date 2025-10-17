/// ==========================================
/// File Name:    StoryPartService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for story part-related business logic
/// Update History:
/// 
/// Updated By: Wyatt Bechtle
/// Update Notes: Ensured that the user has a character before allowing them to contribute to a story.
///               Created createPartForStory method to encapsulate logic for creating a new story part,
///               including order calculation and character validation. (Had to create new method to 
///               align with MVC Structure, but some with a little more knowledge on this could probably
///               refactor)
/// ==========================================

package com.loreweave.loreweave.service;

import java.util.Optional;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.StoryRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryPartService {

    private final StoryPartRepository storyPartRepository;
    private final StoryRepository storyRepository;

    public StoryPartService(StoryPartRepository storyPartRepository,
                            StoryRepository storyRepository) {
        this.storyPartRepository = storyPartRepository;
        this.storyRepository = storyRepository;
    }
    @Transactional
    public StoryPart addStoryPart(StoryPart storyPart, Character character) throws Exception {

        // Require an attached character up front 
        if (character == null) {
            throw new IllegalStateException("You must create/select a character before contributing to a story.");
        }
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
    // Create a new story part for a given story
    @Transactional
    public StoryPart createPartForStory(Long storyId, String content, User user) throws Exception {

        // Fetch the story
        var story = storyRepository.findById(storyId).orElseThrow();

        // Require an attached character up front
        var character = user.getCharacter();
        if (character == null) {
            throw new IllegalStateException("You must create/select a character before contributing to a story.");
        }
        // Determine the next order number
        int nextOrder = storyPartRepository.findMaxPartOrderForStory(storyId) + 1;

        /* Create and save the new story part */
        StoryPart sp = new StoryPart(story, character, content, nextOrder);
        sp.setAuthor(user);
        return addStoryPart(sp, character);
    }

    public Optional<StoryPart> getStoryPartById(Long id) {
        return storyPartRepository.findById(id);
    }

    public Optional<StoryPart> getStoryPartByIdWithContributorAndUser(Long id) {
        return storyPartRepository.findByIdWithContributorUserAndStory(id);
    }

}
