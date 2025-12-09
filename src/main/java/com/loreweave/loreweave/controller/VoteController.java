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
 /// Updated By: Jamie Coker on 2025-10-22
/// Update Notes:
/// - Added validation and error handling for Week 3 tasks.
/// - Added LV001-LV005 error codes for structured backend errors.
/// - Added self-vote prevention (LV003).
/// - Added invalid vote type handling and fallback (LV004).
/// - Added missing StoryPart handling with 404 redirect (LV002).
/// - Added transaction failure safety wrapper (LV005).
/// - Unified all redirects to append ?error=CODE for UI display.
/// - Improved duplicate vote detection with explicit error code.


/// Updated By: Wyatt Bechtle
/// Update Notes: Added notification logic to inform the contributor when their story part receives a vote.
/// 
 */



import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.LoreVote.VoteType;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.CharacterRepository;
import com.loreweave.loreweave.repository.LoreVoteRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.dto.ws.NotificationView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private static final String ERROR_DUPLICATE = "LV001";
    private static final String ERROR_NOT_FOUND = "LV002";
    private static final String ERROR_SELF_VOTE = "LV003";
    private static final String ERROR_INVALID_TYPE = "LV004";
    private static final String ERROR_TRANSACTION = "LV005";


    private final LoreVoteRepository loreVoteRepository;
    private final StoryPartRepository storyPartRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public VoteController(LoreVoteRepository loreVoteRepository,
                          StoryPartRepository storyPartRepository,
                          UserRepository userRepository,
                          CharacterRepository characterRepository,
                          NotificationService notificationService,
                          SimpMessagingTemplate simpMessagingTemplate) {
        this.loreVoteRepository = loreVoteRepository;
        this.storyPartRepository = storyPartRepository;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    /**
     * Cast a vote (POSITIVE or NEGATIVE) on a story part.
     * This endpoint now also handles the transaction logic directly within the LoreVote entity.
     */
    @PostMapping("/{storyPartId}")
    @Transactional
    public org.springframework.http.ResponseEntity<Void> castVote(
            @PathVariable Long storyPartId,
            @RequestParam(defaultValue = "POSITIVE") LoreVote.VoteType type) {

        // --- 1) Authentication ---
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var voter = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // --- 2) StoryPart Exists? (LV002) ---
        var part = storyPartRepository.findById(storyPartId)
                .orElse(null);

        if (part == null) {
            var location = java.net.URI.create("/stories?error=" + ERROR_NOT_FOUND);
            return org.springframework.http.ResponseEntity.status(302).location(location).build();
        }

        // --- 3) Self-vote prevention (LV003) ---
        var contributor = part.getContributor();
        if (contributor == null) {
            var location = java.net.URI.create("/stories?error=" + ERROR_NOT_FOUND);
            return org.springframework.http.ResponseEntity.status(302).location(location).build();
        }
        if (contributor.getUser().getId().equals(voter.getId())) {
            var location = java.net.URI.create("/errors/cannot-vote-own");
            return org.springframework.http.ResponseEntity.status(302).location(location).build();

        }

        // --- 4) Validate vote type (LV004) ---
        if (type == null) {
            var location = java.net.URI.create("/story-parts/" + storyPartId + "?error=" + ERROR_INVALID_TYPE);
            return org.springframework.http.ResponseEntity.status(400).location(location).build();
        }

        // --- 5) Duplicate vote check (LV001) ---
        var existingVote = loreVoteRepository.findByStoryPartAndVoter(part, voter);
        if (existingVote.isPresent()) {
            var location = java.net.URI.create("/story-parts/" + storyPartId + "?error=" + ERROR_DUPLICATE);
            return org.springframework.http.ResponseEntity.status(409).location(location).build();
        }

        // --- 6) Value (+1 or -1) ---
        int delta = (type == LoreVote.VoteType.POSITIVE) ? +1 : -1;

        try {
            // --- 7) Save transaction embedded in vote ---
            var vote = new LoreVote(part, voter, type);
            vote.setAmount(delta);
            vote.setReceiverId(contributor.getId());
            vote.setStatus("COMPLETED");
            loreVoteRepository.save(vote);

            // --- 8) Update contributor lore points ---
            characterRepository.incrementLorePoints(contributor.getId(), delta);

            // Create a notification for the contributor so they are informed about the vote.
            try {
                if (contributor.getUser() != null) {
                    var recipientUser = contributor.getUser();
                    String msg = String.format("Your story part received a %s vote from %s", type == VoteType.POSITIVE ? "positive" : "negative", voter.getUsername());
                    String link = "/story-parts/" + storyPartId; // link directly to the story part page
                    Notification n = new Notification(recipientUser, voter, msg, link);
                    Notification saved = notificationService.createNotification(n);
                    // Send WebSocket notification to the recipient user (best-effort)
                    try {
                        simpMessagingTemplate.convertAndSendToUser(
                                recipientUser.getUsername(),
                                "/queue/notifications",
                                new NotificationView(saved.getMessage(), voter.getUsername(), saved.getCreatedAt().toString())
                        );
                    } catch (Exception e) {
                        // swallow websocket errors; notification remains persisted
                    }
                }
            } catch (Exception e) {
                // Intentionally swallow notification-related errors to avoid affecting voting flow
            }

        } catch (Exception ex) {
            // Transaction failure (LV005)
            var location = java.net.URI.create("/story-parts/" + storyPartId + "?error=" + ERROR_TRANSACTION);
            return org.springframework.http.ResponseEntity.status(500).location(location).build();
        }

        // --- 9) Success redirect ---
        var location = java.net.URI.create("/story-parts/" + storyPartId + "?success=1");
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