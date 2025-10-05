/// ==========================================
/// File Name:    ProfileController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-03
/// Purpose:      Handles profile page requests
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
public class ProfileController {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final StoryRepository storyRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository,
                             CharacterRepository characterRepository,
                             StoryRepository storyRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
        this.storyRepository = storyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = currentUser();
        model.addAttribute("user", user);

        Character character = characterRepository.findByUser(user).orElse(null);
        model.addAttribute("character", character);

        List<Story> createdStories = storyRepository.findStoriesCreatedByUser(user.getId());
        List<Story> contributedStories = storyRepository.findStoriesContributedByUser(user.getId());
        model.addAttribute("createdStories", createdStories);
        model.addAttribute("contributedStories", contributedStories);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        model.addAttribute("user", currentUser());
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User formUser, BindingResult result, Model model) {
        User dbUser = currentUser();
        if (result.hasErrors()) {
            model.addAttribute("user", formUser);
            return "profile-edit";
        }
        dbUser.setFirstName(formUser.getFirstName());
        dbUser.setLastName(formUser.getLastName());
        dbUser.setEmail(formUser.getEmail());
        dbUser.setUsername(formUser.getUsername());

        if (formUser.getPassword() != null && !formUser.getPassword().isBlank()) {
            dbUser.setPassword(passwordEncoder.encode(formUser.getPassword()));
        }
        userRepository.save(dbUser);
        return "redirect:/profile?updated=true";
    }

    // Placeholders for now
    @GetMapping("/messages") public String messages() { return "messages"; }
    @GetMapping("/stories")  public String stories()  { return "stories"; }
    @GetMapping("/characters") public String characters() { return "characters"; }
}