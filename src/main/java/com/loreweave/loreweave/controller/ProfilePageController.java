/// ==========================================
/// File Name:    ProfilePageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-03
/// Purpose:      Controller for handling user profile page requests
///               and profile editing functionality.
/// 
///  Updated By: 
///  Update Notes:
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.repository.CharacterRepository;
import com.loreweave.loreweave.repository.StoryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProfilePageController {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final StoryRepository storyRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfilePageController(UserRepository userRepository,
                             CharacterRepository characterRepository,
                             StoryRepository storyRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
        this.storyRepository = storyRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Helper method to get the currently authenticated user
    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        return userRepository.findByUsername(username).orElseThrow();
    }
    // Display user profile page
    @GetMapping("/profile")
    public String profile(Model model) {

        // Get current user
        User user = currentUser();
        model.addAttribute("user", user);

        // Get associated character
        Character character = characterRepository.findByUser(user).orElse(null);
        model.addAttribute("character", character);

        // Get stories created and contributed to by the user
        List<Story> createdStories = storyRepository.findStoriesCreatedByUser(user.getId());
        List<Story> contributedStories = storyRepository.findStoriesContributedByUser(user.getId());
        model.addAttribute("createdStories", createdStories);
        model.addAttribute("contributedStories", contributedStories);

        return "profile";
    }
    // Display profile edit form
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {

        // Pre-fill form with current user data
        model.addAttribute("user", currentUser());
        return "profile-edit";
    }
    // Handle profile update form submission
    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User formUser, BindingResult result, Model model) {
        User dbUser = currentUser();

        // Validate form data
        if (result.hasErrors()) {
            model.addAttribute("user", formUser);
            return "profile-edit";
        }
        // Update user details
        dbUser.setFirstName(formUser.getFirstName());
        dbUser.setLastName(formUser.getLastName());
        dbUser.setEmail(formUser.getEmail());
        dbUser.setUsername(formUser.getUsername());
        
        // Update password if provided
        if (formUser.getPassword() != null && !formUser.getPassword().isBlank()) {
            dbUser.setPassword(passwordEncoder.encode(formUser.getPassword()));
        }
        userRepository.save(dbUser);
        return "redirect:/profile?updated=true";
    }
}