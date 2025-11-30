package com.loreweave.loreweave.repository;


/// ==========================================
/// File Name:    LoreVoteRepository.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-12
/// Purpose:      Repository interface for managing LoreVote records.
///                Handles both voting and embedded transaction logic.
/// Updated By: Jamie Coker
/// Updated On: 2025-11-30
/// Update Notes: Added existsByStoryPartIdAndVoterId method to support
///               optimized duplicate-vote validation (LV001) in Week 3.
///               No other repository logic changes required.

/// ==========================================

import com.loreweave.loreweave.model.LoreVote;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoreVoteRepository extends JpaRepository<LoreVote, Long> {

    // Retrieve all votes for a given story part
    List<LoreVote> findByStoryPart(StoryPart storyPart);

    // Retrieve all votes cast by a specific user
    List<LoreVote> findByVoter(User voter);

    // Check if a user already voted on a specific story part
    Optional<LoreVote> findByStoryPartAndVoter(StoryPart storyPart, User voter);

    // Faster duplicate-check for Week 3 validation (LV001)
    boolean existsByStoryPartIdAndVoterId(Long storyPartId, Long voterId);

}
