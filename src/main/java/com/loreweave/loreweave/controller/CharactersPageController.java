/// ==========================================
/// File Name:    CharactersPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-08
/// Purpose:      Handles character listing requests
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added User and Story repositories to fetch and display
///                     stories created and contributed to by a character's owner.
///                 Implemented GET and POST handlers for creating new characters,
///                     including server-side validation to enforce one character per user.
///                 Added flash messages for user feedback on character creation.
///                 Improved code comments and structure for clarity.
///                 Updated character-detail route to include story lists.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.CharacterRepository;
import com.loreweave.loreweave.repository.StoryRepository;
import com.loreweave.loreweave.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class CharactersPageController {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    public CharactersPageController(CharacterRepository characterRepository
                                      , UserRepository userRepository,
                                      StoryRepository storyRepository) {
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
    }
    // Characters listing page
    @GetMapping("/characters")
    public String characters(Model model) {
        List<Character> characters = characterRepository.findAll();
        model.addAttribute("characters", characters);
        return "characters"; 
    }
    // Character detail page
    @GetMapping("/characters/{id}")
    public String character(@PathVariable Long id, Model model) {

        // Fetch character or 404
        var ch = characterRepository.findById(id).orElseThrow();
        model.addAttribute("character", ch);

        // Default to empty lists if no owner
        List<Story> createdStories = Collections.emptyList();
        List<Story> contributedStories = Collections.emptyList();

        // If character has an owner, fetch their stories
        if (ch.getUser() != null && ch.getUser().getId() != null) {
            Long userId = ch.getUser().getId();

            // Fetch stories created and contributed to by this user
            createdStories = storyRepository.findStoriesCreatedByUser(userId);
            contributedStories = storyRepository.findStoriesContributedByUser(userId);
        }
        // Add to model
        model.addAttribute("createdStories", createdStories);
        model.addAttribute("contributedStories", contributedStories);

        return "character-detail";
    }
    // New character form (GET)
    @GetMapping("/characters/new")
    public String newCharacter(Authentication auth, Model model, RedirectAttributes ra) {
        
        // Resolve current user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);
        
        // Enforce one character per user (server-side guard)
        if (characterRepository.findByUser(owner).isPresent()) {

            // Redirect with warning if user already has a character
            ra.addFlashAttribute("warning", "Only one character per user.");
            return "redirect:/characters";
        }
        // If not already present (e.g. after validation error), add a blank character
        if (!model.containsAttribute("character")) {
            model.addAttribute("character", new Character("", "", 0, owner));
        }
        return "character-new";
    }
    // New character form submission (POST) 
    @PostMapping("/characters/new")
    public String createCharacter(@RequestParam("name") String name,
                                @RequestParam("discription") String description,
                                Authentication auth,
                                RedirectAttributes ra) {
                                    
        // Resolve current user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        // Enforce one character per user (server-side guard)
        if (characterRepository.findByUser(owner).isPresent()) {

            // Redirect with warning if user already has a character
            ra.addFlashAttribute("warning", "You already have a character. Only one per user.");
            return "redirect:/characters";
        }
        // Populate and save new character
        Character character = new Character(name.trim(),
                                            description.trim(),
                                            0,
                                            owner);
        Character saved = characterRepository.save(character);

        // Redirect to new character with success message
        ra.addFlashAttribute("success", "Character created!");
        return "redirect:/characters/" + saved.getId();
    }


}
