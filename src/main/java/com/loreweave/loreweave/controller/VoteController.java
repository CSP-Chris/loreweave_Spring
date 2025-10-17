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
/// 
/// Updated By:   Wyatt Bechtle
/// Update Notes: Refactored to use @RestController and return ResponseEntity to match RESTController style.
///               Added IncrementLorePoints method from CharacterRepository to adjust lore points directly.
///               Added @Transactional to interact with DB. Redirects user back to story part after voting.
 ///              Also integrated CharacterRepository to adjust lore points directly.
 ///Updated By: Jamie Coker 10/17/2025
 Update Notes: Add lines to check if the user already voted on this story part
 */


import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.LoreVote.VoteType;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.CharacterRepository;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final LoreVoteRepository loreVoteRepository;
    private final StoryPartRepository storyPartRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;

    public VoteController(LoreVoteRepository loreVoteRepository,
                          StoryPartRepository storyPartRepository,
                          UserRepository userRepository,
                          CharacterRepository characterRepository) {
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartRepository = storyPartRepository;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
    }


    /**
     * Cast a vote (POSITIVE or NEGATIVE) on a story part.
     * This endpoint now also handles the transaction logic directly within the LoreVote entity.
     */
    @PostMapping("/{storyPartId}")
    @Transactional
    public org.springframework.http.ResponseEntity<Void> castVote(@PathVariable Long storyPartId,
                                                                 @RequestParam(defaultValue = "POSITIVE") LoreVote.VoteType type) {
        // Who is voting
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var voter = userRepository.findByUsername(auth.getName()).orElseThrow();

        // Target part and author character
        var part = storyPartRepository.findById(storyPartId).orElseThrow();
        var contributor = part.getContributor();
        if (contributor == null) throw new RuntimeException("Story part has no contributor");

        // Check if the user already voted on this story part
        var existingVote = loreVoteRepository.findByStoryPartAndVoter(part, voter);
        if (existingVote.isPresent()) {
            // Return 409 Conflict with redirect back to the story part
            var location = java.net.URI.create("/story-parts/" + storyPartId);
            return org.springframework.http.ResponseEntity.status(409).location(location).build();
        }

        // delta: +1 for POSITIVE, -1 for NEGATIVE
        int delta = (type == LoreVote.VoteType.POSITIVE) ? +1 : -1;

        // Record a vote 
        var vote = new LoreVote(part, voter, type);
        vote.setAmount(delta);                 // +1 or -1
        vote.setReceiverId(contributor.getId());
        vote.setStatus("COMPLETED");
        loreVoteRepository.save(vote);

        // Adjust lore points
        characterRepository.incrementLorePoints(contributor.getId(), delta);

        // Redirect back to where the user came from
        // REST based but could be simplified with MVC controller type
        var location = java.net.URI.create("/story-parts/" + storyPartId);
        return org.springframework.http.ResponseEntity.status(303).location(location).build();
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