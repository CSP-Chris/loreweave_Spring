/// ==========================================
/// File Name:    StoryPartPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-11
/// Purpose:      Handles story part page requests
///  Updated By:  Wyatt Bechtle
///  Update Notes: Use fetch-join repo method to avoid LazyInitialization in view
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added StoryPartService to handle logic for creating story parts.
///                 Implemented POST handler for creating new story parts with user authentication.
///                 Added GET handler to show form for creating new story parts.
///                 Improved code comments and structure for clarity.
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.loreweave.loreweave.service.StoryPartService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoryPartPageController {

    private final StoryPartRepository storyPartRepository;
    private final LoreVoteRepository loreVoteRepository;
    private final StoryPartService storyPartService;
    private final UserRepository userRepository;

    public StoryPartPageController(StoryPartRepository storyPartRepository,
                                   LoreVoteRepository loreVoteRepository,
                                   StoryPartService storyPartService,
                                   UserRepository userRepository) {
        this.storyPartRepository = storyPartRepository;
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartService = storyPartService;
        this.userRepository = userRepository;
    }

    // Display a specific story part
    @GetMapping("/story-parts/{id:\\d+}")
    public String storyPart(@PathVariable Long id, Model model) {

        // Fetch the story part with contributor.user 
        StoryPart part = storyPartRepository
                .findByIdWithContributorUserAndStory(id)
                .orElseThrow();

        // Calculate vote score
        long positives = loreVoteRepository.findByStoryPart(part).stream()
                .filter(v -> v.getVoteType() == LoreVote.VoteType.POSITIVE).count();
        long negatives = loreVoteRepository.findByStoryPart(part).stream()
                .filter(v -> v.getVoteType() == LoreVote.VoteType.NEGATIVE).count();

        // Add attributes to the model
        model.addAttribute("part", part);
        model.addAttribute("voteScore", positives - negatives);
        model.addAttribute("totalVotes", positives + negatives);

        // For now, composing new parts is disabled
        model.addAttribute("allowCompose", false);

        return "story-part";
    }
    // Handle creating a new story part
    @PostMapping("/story-parts")
    public String createPart(@RequestParam Long storyId,
                             @RequestParam String content,
                             Authentication auth,
                             RedirectAttributes ra) throws Exception {

        // Resolve current user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElse(null);
        
        // Create the story part
        storyPartService.createPartForStory(storyId, content, user);
        return "redirect:/story/" + storyId;
    }
    // Show form to create a new story part
    @GetMapping("/story-parts/new")
    public String newPartForm(@RequestParam Long storyId, Model model) {

        // Add storyId to the model for the form
        model.addAttribute("storyId", storyId);
        return "story-part-new"; 
    }
}
