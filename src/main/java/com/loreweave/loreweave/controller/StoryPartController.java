/// ==========================================
/// File Name:    StoryPartController.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Controller for handling story part creation
/// Update History: 
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Set story part author to authenticated user
///  Added By:     Jamie Coker
/// Added On:     2025-10-13
///  Update Notes: Added GET mapping to display individual story parts.
///              Passes userHasVoted flag and vote stats to the template
///              for disabling vote buttons if the user already voted.
/// ==========================================

package com.loreweave.loreweave.controller;


import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.StoryPartService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StoryPartController {

    private final StoryPartService storyPartService;
    private final LoreVoteRepository loreVoteRepository;
    private final UserRepository userRepository;

    //  Constructor injection — ensures all fields are properly initialized
    public StoryPartController(StoryPartService storyPartService,
                               LoreVoteRepository loreVoteRepository,
                               UserRepository userRepository) {
        this.storyPartService = storyPartService;
        this.loreVoteRepository = loreVoteRepository;
        this.userRepository = userRepository;
    }

    // NEW: Display a single story part and voting info
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
}
