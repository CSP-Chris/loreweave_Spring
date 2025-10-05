/// ==========================================
/// File Name:    StoryRepository.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-02
/// Purpose:      Repository interface for Story entity
/// Update History:
/// 
/// ==========================================
/// Not 100% sure if the queries are optimal, but they work for now.
package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    @Query("""
           SELECT s
           FROM Story s
           WHERE s.creator.user.id = :userId
           ORDER BY s.lastUpdatedAt DESC
           """)
    List<Story> findStoriesCreatedByUser(@Param("userId") Long userId);

    @Query("""
           SELECT DISTINCT sp.story
           FROM StoryPart sp
           WHERE sp.contributor.user.id = :userId
           ORDER BY sp.story.lastUpdatedAt DESC
           """)
    List<Story> findStoriesContributedByUser(@Param("userId") Long userId);
}