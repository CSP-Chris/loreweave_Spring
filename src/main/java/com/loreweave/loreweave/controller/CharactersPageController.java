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
/// 
///   Updated By:  Wyatt Bechtle
/// Update Notes:  Added lore point recalculation on character listing and detail pages.
/// Updated By:    Jamie Coker on 2025-12-07
/// ///  Update Notes:  Updated /characters listing to:
/// ///                 • Display the logged-in user's character separately at the top.
/// ///                 • List all other characters below under “Other Characters.”
/// —                 • Hide the "Add Character" button if the user already has a character.
/// ///                 • Added controller attributes:
/// ///                       currentUserCharacter
/// ///                       otherCharacters
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
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class CharactersPageController {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    public CharactersPageController(CharacterRepository characterRepository,
                                    UserRepository userRepository,
                                    StoryRepository storyRepository) {
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
    }

    // ==========================================
    // CHARACTER LISTING PAGE
    // ==========================================
    @GetMapping("/characters")
    public String characters(Authentication auth, Model model) {

        // Get logged-in user
        String username = auth != null ? auth.getName() : null;
        User user = username != null
                ? userRepository.findByUsername(username).orElse(null)
                : null;

        // Get user's character (or null)
        Character currentUserCharacter = (user != null)
                ? characterRepository.findByUser(user).orElse(null)
                : null;

        // Fetch ALL characters + recalc lore points
        var allCharacters = characterRepository.findAll();
        for (var ch : allCharacters) {
            ch.setLorePoints(characterRepository.sumVotesForCharacter(ch.getId()));
        }

        // Filter out user's character from "other" list
        List<Character> otherCharacters = allCharacters.stream()
                .filter(ch -> currentUserCharacter == null || !ch.getId().equals(currentUserCharacter.getId()))
                .toList();

        // Add attributes for the updated characters.html template
        model.addAttribute("currentUserCharacter", currentUserCharacter);
        model.addAttribute("otherCharacters", otherCharacters);

        return "characters";
    }

    // ==========================================
    // CHARACTER DETAIL PAGE
    // ==========================================
    @GetMapping("/characters/{id}")
    public String character(@PathVariable("id") Long id, Model model) {

        // Fetch character and recalc lore points
        var ch = characterRepository.findById(id).orElseThrow();
        int pts = characterRepository.sumVotesForCharacter(ch.getId());
        ch.setLorePoints(pts);
        model.addAttribute("character", ch);

        // Default empty lists
        List<Story> createdStories = Collections.emptyList();
        List<Story> contributedStories = Collections.emptyList();

        // If character has an owner, fetch their stories
        if (ch.getUser() != null && ch.getUser().getId() != null) {
            Long userId = ch.getUser().getId();
            createdStories = storyRepository.findStoriesCreatedByUser(userId);
            contributedStories = storyRepository.findStoriesContributedByUser(userId);
        }

        model.addAttribute("createdStories", createdStories);
        model.addAttribute("contributedStories", contributedStories);

        return "character-detail";
    }

    // ==========================================
    // NEW CHARACTER FORM (GET)
    // ==========================================
    @GetMapping("/characters/new")
    public String newCharacter(Authentication auth, Model model, RedirectAttributes ra) {

        // Resolve user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        // Enforce 1 character per user
        if (characterRepository.findByUser(owner).isPresent()) {
            ra.addFlashAttribute("warning", "Only one character per user.");
            return "redirect:/characters";
        }

        // Provide blank character for form
        if (!model.containsAttribute("character")) {
            model.addAttribute("character", new Character("", "", 0, owner));
        }

        return "character-new";
    }

    // ==========================================
    // NEW CHARACTER SUBMISSION (POST)
    // ==========================================
    @PostMapping("/characters/new")
    public String createCharacter(@RequestParam("name") String name,
                                  @RequestParam("discription") String description,
                                  Authentication auth,
                                  RedirectAttributes ra) {

        // Resolve user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User owner = userRepository.findByUsername(username).orElse(null);

        // Enforce 1 character per user
        if (characterRepository.findByUser(owner).isPresent()) {
            ra.addFlashAttribute("warning", "You already have a character. Only one per user.");
            return "redirect:/characters";
        }

        // Save new character
        Character character = new Character(name.trim(), description.trim(), 0, owner);
        Character saved = characterRepository.save(character);

        ra.addFlashAttribute("success", "Character created!");
        return "redirect:/characters/" + saved.getId();
    }
}