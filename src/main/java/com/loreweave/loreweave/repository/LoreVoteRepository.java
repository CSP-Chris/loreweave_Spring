package com.loreweave.loreweave.repository;

/*
 ==========================================
 File Name:    LoreVoteRepository.java
 Created By:   Jamie Coker
 Created On:   2025-10-10
 Purpose:      Repository for managing LoreVote entities linked to StoryParts.
               Supports lookups by storyPart and voter for preventing duplicates.
 ==========================================
 */

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoreVoteRepository extends JpaRepository<LoreVote, Long> {

    // All votes for a story part
    List<LoreVote> findByStoryPart(StoryPart storyPart);

    // All votes cast by a user
    List<LoreVote> findByVoter(User voter);

    // Check if user already voted on a specific story part
    Optional<LoreVote> findByStoryPartAndVoter(StoryPart storyPart, User voter);
}
