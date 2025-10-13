/// ==========================================
/// File Name:    StoryPartRepository.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      JPA repository for the StoryPart entity
/// Update History:
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added a custom query method to fetch StoryParts 
///                 with their Contributors and Users to avoid 
///                 LazyInitializationException, which was causing an issue.
///  
/// ==========================================

package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.StoryPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryPartRepository extends JpaRepository<StoryPart, Long> {
    @Query("SELECT sp FROM StoryPart sp WHERE sp.story.id = :storyId ORDER BY sp.createdAt DESC")
    List<StoryPart> findLatestStoryPartsForStory(Long storyId);

    // Original method, may still be useful in some contexts
    List<StoryPart> findByStoryIdOrderByPartOrderAsc(Long storyId);
    
    // Custom query to fetch StoryParts along with their Contributors and Users
    // to avoid LazyInitializationException.
    @Query("""
           SELECT sp
           FROM StoryPart sp
           JOIN FETCH sp.contributor c
           JOIN FETCH c.user u
           WHERE sp.story.id = :storyId
           ORDER BY sp.partOrder ASC
           """)
    List<StoryPart> fetchByStoryIdOrderByPartOrderAsc(@Param("storyId") Long storyId);
}