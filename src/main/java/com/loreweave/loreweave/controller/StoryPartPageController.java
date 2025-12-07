/// ==========================================
/// File Name:    StoryPartPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-11
/// Purpose:      Handles story part page requests
///  Updated By:  Wyatt Bechtle
///  Update Notes: Use fetch-join repo method to avoid LazyInitialization in view
///
///  Updated By:   Wyatt Bechtle
///  Update Notes: Added StoryPartService to handle logic for creating story parts.
///                Implemented POST handler for creating new story parts with user authentication.
///                Added GET handler to show form for creating new story parts.
///                Improved code comments and structure for clarity.
///
///  Updated By:   Jamie Coker on 2025-11-30
///  Update Notes: Wrapped POST /story-parts handler in try/catch to detect when the
///                user attempts to add two consecutive parts to the same story.
///                On that error, return story-part-turn-error.html with the story
///                and a friendly error message instead of redirecting.
/// Updated By:   Jamie Coker on 2025-12-07
/// /// Update Notes: Added turn-order validation to GET /story-parts/new so users
/// ///               cannot access the creation form if they wrote the last part.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.StoryPartService;
import com.loreweave.loreweave.service.StoryService;   //  NEW

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final StoryService storyService;  //  NEW

    public StoryPartPageController(StoryPartRepository storyPartRepository,
                                   LoreVoteRepository loreVoteRepository,
                                   StoryPartService storyPartService,
                                   UserRepository userRepository,
                                   StoryService storyService) {  //  NEW param
        this.storyPartRepository = storyPartRepository;
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartService = storyPartService;
        this.userRepository = userRepository;
        this.storyService = storyService;     //  NEW assignment
    }

    // Handle creating a new story part
    @PostMapping("/story-parts")
    public String createPart(@RequestParam("storyId") Long storyId,
                             @RequestParam("content") String content,
                             Authentication auth,
                             RedirectAttributes ra,
                             Model model) throws Exception {   //  added Model

        // Resolve current user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        try {
            // Create the story part (enforces: must have character, no two turns in a row)
            storyPartService.createPartForStory(storyId, content, user);

            // On success, go back to the story page
            return "redirect:/story/" + storyId;

        } catch (Exception ex) {
            String msg = ex.getMessage() != null
                    ? ex.getMessage()
                    : "An error occurred while adding your story part.";

            // If this is the "not your turn" error, show the custom error page
            if (msg.contains("You cannot add another story part until another user contributes.")) {
                var story = storyService.getStoryById(storyId);
                model.addAttribute("story", story);
                model.addAttribute("errorMessage", msg);

                return "story-part-turn-error";
            }

            // For other errors, rethrow so they're visible during development
            throw ex;
        }
    }

    // Show form to create a new story part — BUT first check turn order
    @GetMapping("/story-parts/new")
    public String newPartForm(@RequestParam("storyId") Long storyId,
                              Authentication auth,
                              Model model) {

        // Resolve current user
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        // Load the story
        var story = storyService.getStoryById(storyId);

        // Load the last part of the story
        var lastPartOpt = storyPartRepository.findTopByStoryIdOrderByPartOrderDesc(storyId);

        if (lastPartOpt.isPresent()) {
            var lastPart = lastPartOpt.get();

            // If the logged-in user wrote the last part → NOT THEIR TURN
            if (lastPart.getAuthor() != null &&
                    lastPart.getAuthor().getId().equals(user.getId())) {

                model.addAttribute("story", story);
                model.addAttribute(
                        "errorMessage",
                        "You wrote the last part of this story. Another author must contribute before you can add a new part."
                );

                return "story-part-turn-error";
            }
        }

        // Otherwise allow the form to load
        model.addAttribute("storyId", storyId);
        return "story-part-new";
    }
}
