package com.loreweave.loreweave.controller;

/*
 ==========================================
 File Name:    VoteController.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Allows authenticated users to cast votes on StoryParts.
               Integrated with LoreVote entity and TransactionService.
 ==========================================
 */

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.LoreVote.VoteType;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.model.Transaction;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.TransactionService;
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
    private final TransactionService transactionService;

    public VoteController(LoreVoteRepository loreVoteRepository,
                          StoryPartRepository storyPartRepository,
                          UserRepository userRepository,
                          TransactionService transactionService) {
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartRepository = storyPartRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
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

        // Prevent duplicate votes
        Optional<LoreVote> existing = loreVoteRepository.findByStoryPartAndVoter(storyPart, voter);
        if (existing.isPresent()) {
            return "You have already voted on this story part.";
        }

        // Create and save new vote
        LoreVote vote = new LoreVote(storyPart, voter, type);
        loreVoteRepository.save(vote);

        // Optional: reward author with a transaction (for POSITIVE votes)
        if (type == VoteType.POSITIVE) {
            Transaction tx = new Transaction();
            tx.setSenderId(voter.getId());
            tx.setReceiverId(1L); // TODO: replace with actual story author ID

            //Once we identify which User owns each StoryPart, replace the above line:
            //tx.setReceiverId(storyPart.getAuthor().getId());
            //That will make the point-reward system dynamic.

            tx.setAmount(1.0);
            tx.setStatus("PENDING");
            transactionService.createTransaction(tx);
        }

        return "Vote recorded successfully!";
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
     * Get all votes made by the logged-in user.
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