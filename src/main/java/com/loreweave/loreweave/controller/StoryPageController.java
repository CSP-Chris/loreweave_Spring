/// ==========================================
/// File Name:    StoryPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-10
/// Purpose:      Controller for handling story-related web pages.
/// 
///  Updated By: 
///  Update Notes:
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.StoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class StoryPageController {

    private final StoryRepository storyRepository;
    private final StoryPartRepository storyPartRepository;

    public StoryPageController(StoryRepository storyRepository,
                               StoryPartRepository storyPartRepository) {
        this.storyRepository = storyRepository;
        this.storyPartRepository = storyPartRepository;
    }
    // Route to display all stories
    @GetMapping("/stories")
    public String stories(Model model) {

        // Fetch all stories with their creators and users, ordered by last updated date
        List<Story> stories = storyRepository.findAllWithCreatorAndUserOrderByLastUpdatedAtDesc();
        model.addAttribute("stories", stories);
        return "stories"; 
    }
    // Route to display a specific story by ID
    @GetMapping("/story/{id}")
    public String story(@PathVariable Long id, Model model) {

        // Fetch the story with its creator and users
        Story story = storyRepository.findByIdWithCreatorAndUser(id).orElseThrow();

        // Fetch all parts of the story ordered by part order
        List<StoryPart> parts = storyPartRepository.fetchByStoryIdOrderByPartOrderAsc(id);
        
        model.addAttribute("story", story);
        model.addAttribute("parts", parts);
        return "story"; // renders templates/story.html
    }
}
