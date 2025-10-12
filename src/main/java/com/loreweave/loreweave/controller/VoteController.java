package com.loreweave.loreweave.controller;

/*
 ==========================================
 File Name:    VoteController.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Allows authenticated users to cast votes on StoryParts.
               Integrated with LoreVote entity and TransactionService.
    Updated By:   Jamie Coker on 2025-10-12
/// Update Notes: Removed TransactionService calls and added direct
///               transaction tracking to LoreVote entity.
 ==========================================
 */

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.LoreVote.VoteType;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final LoreVoteRepository loreVoteRepository;
    private final StoryPartRepository storyPartRepository;
    private final UserRepository userRepository;

    public VoteController(LoreVoteRepository loreVoteRepository,
                          StoryPartRepository storyPartRepository,
                          UserRepository userRepository) {
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartRepository = storyPartRepository;
        this.userRepository = userRepository;
    }

    /**
     * Cast a vote (POSITIVE or NEGATIVE) on a story part.
     */
    @PostMapping("/{storyPartId}")
    public String castVote(@PathVariable Long storyPartId,
                           @RequestParam(defaultValue = "POSITIVE") VoteType type) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User voter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StoryPart storyPart = storyPartRepository.findById(storyPartId)
                .orElseThrow(() -> new RuntimeException("Story part not found"));

        Optional<LoreVote> existing = loreVoteRepository.findByStoryPartAndVoter(storyPart, voter);
        if (existing.isPresent()) {
            return "You have already voted on this story part.";
        }

        // Create new vote (with embedded transaction)
        LoreVote vote = new LoreVote(storyPart, voter, type);

        // 🔽 ADD: merged transaction logic directly into LoreVote
        vote.setAmount(type == VoteType.POSITIVE ? 1.0 : 0.0);
        vote.setReceiverId(storyPart.getAuthor().getId()); // assumes StoryPart has getAuthor()
        vote.setStatus("COMPLETED");

        loreVoteRepository.save(vote);

        return "Vote recorded and transaction completed!";
    }

    /**
     * Count positive votes for a story part.
     */
    @GetMapping("/storypart/{id}/count")
    public long countVotes(@PathVariable Long id) {
        StoryPart storyPart = storyPartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Story part not found"));
        return loreVoteRepository.findByStoryPart(storyPart).stream()
                .filter(v -> v.getVoteType() == VoteType.POSITIVE)
                .count();
    }

    /**
     * Get all votes (with transaction data) made by the logged-in user.
     */
    @GetMapping("/myvotes")
    public List<LoreVote> getMyVotes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User voter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loreVoteRepository.findByVoter(voter);
    }
}