package com.loreweave.loreweave.repository;
/// ==========================================
/// File Name:    StoryRepository.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-02
/// Purpose:      Repository interface for Story entity
/// Update History:
/// 
/// 
///  Updated By:     Wyatt Bechtle
///  Update Notes:   queries added to fetch stories with creators and users
///                  and by id
/// 
/// ==========================================


import com.loreweave.loreweave.model.Story;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    // Fetch stories created by a specific user
    @Query("""
           SELECT s
           FROM Story s
           WHERE s.creator.user.id = :userId
           ORDER BY s.lastUpdatedAt DESC
           """)
    List<Story> findStoriesCreatedByUser(@Param("userId") Long userId);

    // Fetch stories where the user has contributed via StoryParts
    @Query("""
           SELECT DISTINCT sp.story
           FROM StoryPart sp
           WHERE sp.contributor.user.id = :userId
           ORDER BY sp.story.lastUpdatedAt DESC
           """)
    List<Story> findStoriesContributedByUser(@Param("userId") Long userId);

    // Fetch stories with their creators and users, ordered by last updated timestamp
    @Query("""
           SELECT DISTINCT s
           FROM Story s
           LEFT JOIN FETCH s.creator c
           LEFT JOIN FETCH c.user u
           ORDER BY s.lastUpdatedAt DESC
           """)
    List<Story> findAllWithCreatorAndUserOrderByLastUpdatedAtDesc();

    // Fetch a story by ID with its creator and user
    @Query("""
           SELECT s
           FROM Story s
           LEFT JOIN FETCH s.creator c
           LEFT JOIN FETCH c.user u
           WHERE s.id = :id
           """)
    Optional<Story> findByIdWithCreatorAndUser(@Param("id") Long id);
}