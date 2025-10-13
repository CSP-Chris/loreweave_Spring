/// ==========================================
/// File Name:    StoryPartPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-11
/// Purpose:      Handles story part page requests
///  Updated By: 
///  Update Notes:
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StoryPartPageController {

    private final StoryPartRepository storyPartRepository;
    private final LoreVoteRepository loreVoteRepository;

    public StoryPartPageController(StoryPartRepository storyPartRepository,
                                   LoreVoteRepository loreVoteRepository) {
        this.storyPartRepository = storyPartRepository;
        this.loreVoteRepository = loreVoteRepository;
    }
    // Display a specific story part
    @GetMapping("/story-parts/{id}")
    public String storyPart(@PathVariable Long id, Model model) {

        // Fetch the story part
        StoryPart part = storyPartRepository.findById(id).orElseThrow();

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
}
