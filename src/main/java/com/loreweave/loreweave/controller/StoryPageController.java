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
///  /// Updated By:     Jamie Coker on 2025-12-07
/// /// Update Notes:   Added dynamic storyPart count for each story displayed on the /stories page.
/// ///                 Added @Transient partCount mapping and controller logic to populate story thread counts.
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

    // ==========================================
    // Stories listing page — now with part count
    // ==========================================
    @GetMapping("/stories")
    public String stories(Model model) {

        // Fetch all stories with creator + user
        List<Story> stories = storyRepository.findAllWithCreatorAndUserOrderByLastUpdatedAtDesc();

        // Add story part counts to each story
        for (Story story : stories) {
            int partCount = storyPartRepository.findByStoryIdOrderByPartOrderAsc(story.getId()).size();
            story.setPartCount(partCount);   // Story has transient field
        }

        model.addAttribute("stories", stories);
        return "stories";
    }

    // View a single story
    @GetMapping("/story/{id:\\d+}")
    public String story(@PathVariable("id") Long id, Model model) {

        Story story = storyRepository.findByIdWithCreatorAndUser(id).orElseThrow();

        List<StoryPart> parts = storyPartRepository.fetchByStoryIdOrderByPartOrderAsc(id);

        model.addAttribute("story", story);
        model.addAttribute("parts", parts);
        return "story";
    }

    // New Story Form
    @GetMapping("/story/new")
    public String newStory(Authentication auth, RedirectAttributes ra) {

        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        boolean hasCharacter = characterRepository.findByUser(owner).isPresent();
        if (!hasCharacter) {
            ra.addFlashAttribute("warning", "Create your character first.");
            return "redirect:/characters/new";
        }

        return "story-new";
    }

    // Create Story Post Handler
    @PostMapping("/story/new")
    public String createStory(Authentication auth,
                              @RequestParam("title") String title,
                              RedirectAttributes ra) {

        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        Character creator = characterRepository.findByUser(owner).orElse(null);

        if (creator == null) {
            ra.addFlashAttribute("warning", "Create your character first.");
            return "redirect:/characters/new";
        }

        String t = title.trim();
        Story saved = storyService.createStory(new Story(t, creator));

        return "redirect:/story/" + saved.getId();
    }
}