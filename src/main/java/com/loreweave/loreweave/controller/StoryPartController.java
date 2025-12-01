package com.loreweave.loreweave.controller;
/// ==========================================
/// File Name:    StoryPartController.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Controller for handling story part creation
/// Update History:
///
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Set story part author to authenticated user
///
///  Added By:      Jamie Coker
///  Added On:      2025-10-13
///  Update Notes:  Added GET mapping to display individual story parts.
///                 Passes userHasVoted flag and vote stats to the template
///                 for disabling vote buttons if the user already voted.
///
///  Updated By:    Jamie Coker on 2025-11-30
///  Update Notes:  Injected StoryService and added POST mapping for /story-parts
///                 to handle story-part-new.html form submissions. If a user
///                 attempts to add two consecutive parts to the same story, the
///                 controller detects the specific error message from
///                 StoryPartService and returns a dedicated error view
///                 (story-part-turn-error.html) with a helpful message and
///                 a link back to the story.
/// ==========================================


import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.StoryPartService;
import com.loreweave.loreweave.service.StoryService;     // 👈 NEW
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import com.loreweave.loreweave.service.StoryService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StoryPartController {

    private final StoryPartService storyPartService;
    private final LoreVoteRepository loreVoteRepository;
    private final UserRepository userRepository;
    private final StoryService storyService;   // 👈 NEW: used for error page

    //  Constructor injection — ensures all fields are properly initialized
    public StoryPartController(StoryPartService storyPartService,
                               LoreVoteRepository loreVoteRepository,
                               UserRepository userRepository,
                               StoryService storyService) {     // 👈 NEW param
        this.storyPartService = storyPartService;
        this.loreVoteRepository = loreVoteRepository;
        this.userRepository = userRepository;
        this.storyService = storyService;                      // 👈 NEW assignment
    }

    // Display a single story part and voting info
    @GetMapping("/story-parts/{id}")
    public String viewStoryPart(@PathVariable("id") Long id, Model model, Authentication authentication) {

        Optional<StoryPart> optionalPart = storyPartService.getStoryPartByIdWithContributorAndUser(id);
        if (optionalPart.isEmpty()) {
            throw new RuntimeException("Story part not found");
        }
        StoryPart part = optionalPart.get();

        model.addAttribute("part", part);

        boolean userHasVoted = false;
        if (authentication != null && authentication.isAuthenticated()) {
            var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
            userHasVoted = loreVoteRepository.findByStoryPartAndVoter(part, user).isPresent();
        }

        model.addAttribute("userHasVoted", userHasVoted);

        long totalVotes = loreVoteRepository.findByStoryPart(part).size();
        long voteScore = loreVoteRepository.findByStoryPart(part).stream()
                .mapToInt(v -> v.getVoteType() == com.loreweave.loreweave.model.LoreVote.VoteType.POSITIVE ? 1 : -1)
                .sum();

        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("voteScore", voteScore);

        return "story-part";
    }

    @MessageMapping("/story/{storyId}")
    @SendTo("/topic/story/{storyId}")
    public StoryPart addPart(@RequestBody StoryPart storyPart, @AuthenticationPrincipal User user) throws Exception {
        storyPart.setAuthor(user);
        return storyPartService.addStoryPart(storyPart, user.getCharacter());
    }

    @PostMapping("/api/storyparts")
    public ResponseEntity<StoryPart> addPartRest(@RequestBody StoryPart storyPart, @AuthenticationPrincipal User user) {
        try {
            StoryPart newStoryPart = storyPartService.addStoryPart(storyPart, user.getCharacter());
            return ResponseEntity.ok(newStoryPart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
// NEW: Handle form submission from story-part-new.html
// ==========================================
//    @PostMapping("/story-parts")
//    public String addPartFromForm(@RequestParam("storyId") Long storyId,
//                                  @RequestParam("content") String content,
//                                  @AuthenticationPrincipal User user,
//                                  Model model) {
//        try {
//            storyPartService.createPartForStory(storyId, content, user);
//            return "redirect:/story/" + storyId;
//        } catch (Exception ex) {
//            String msg = ex.getMessage() != null
//                    ? ex.getMessage()
//                    : "An error occurred while adding your story part.";
//
//            if (msg.contains("You cannot add another story part until another user contributes.")) {
//                var story = storyService.getStoryById(storyId);
//                model.addAttribute("story", story);
//                model.addAttribute("errorMessage", msg);
//                return "story-part-turn-error";
//            }
//
//            throw new RuntimeException(ex);
//        }
//    }
}
