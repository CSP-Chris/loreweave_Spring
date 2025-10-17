/// ==========================================
/// File Name:    StoryPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-10
/// Purpose:      Controller for handling story-related web pages.
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added regex to @GetMapping to ensure id is numeric
/// 
/// Updated By:     Wyatt Bechtle
/// Update Notes:   Added create story button functionality and validation to ensure user has a character before
///                 creating a story. Redirects to character creation if none exists.  
///                 Implemented Get and Post mappings for /story/new.
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.repository.CharacterRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.StoryRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.StoryService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StoryPageController {

    private final StoryRepository storyRepository;
    private final StoryPartRepository storyPartRepository;
    private final StoryService storyService;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;

    public StoryPageController(StoryRepository storyRepository,
                               StoryPartRepository storyPartRepository,
                               StoryService storyService,
                               UserRepository userRepository,
                               CharacterRepository characterRepository) {
        this.storyRepository = storyRepository;
        this.storyPartRepository = storyPartRepository;
        this.storyService = storyService;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
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
    @GetMapping("/story/{id:\\d+}")
    public String story(@PathVariable Long id, Model model) {

        // Fetch the story with its creator and users
        Story story = storyRepository.findByIdWithCreatorAndUser(id).orElseThrow();

        // Fetch all parts of the story ordered by part order
        List<StoryPart> parts = storyPartRepository.fetchByStoryIdOrderByPartOrderAsc(id);
        
        model.addAttribute("story", story);
        model.addAttribute("parts", parts);
        return "story"; // renders templates/story.html
    }
    // Route to display the new story creation form
    @GetMapping("/story/new")
    public String newStory(Authentication auth, RedirectAttributes ra) {

        // Get user from authentication
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        // Check if user has a character
        boolean hasCharacter = characterRepository.findByUser(owner).isPresent();
        if (!hasCharacter) {
            ra.addFlashAttribute("warning", "Create your character first.");
            return "redirect:/characters/new";
        }
        return "story-new";
    }
    // Route to handle new story creation
    @PostMapping("/story/new")
    public String createStory(Authentication auth, @RequestParam("title") String title, RedirectAttributes ra) {

        // Get user from authentication
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        // Get user's character
        Character creator = characterRepository.findByUser(owner).orElse(null);

        // Validate character existence
        if (creator == null) {
            ra.addFlashAttribute("warning", "Create your character first.");
            return "redirect:/characters/new";
        }
        // Trim title and create story
        String t = title.trim();
        Story saved = storyService.createStory(new Story(t, creator));
        return "redirect:/story/" + saved.getId();
    }
}
