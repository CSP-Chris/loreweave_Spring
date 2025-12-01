package com.loreweave.loreweave.repository;
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
///  Updated By:    Wyatt Bechtle
/// Update Notes:  Added a method to find the maximum partOrder for a given story.
///                This is useful for determining the next partOrder when adding a new StoryPart.
///                Also added a method to fetch a StoryPart by its ID along with its Contributor and User.
///                This helps in scenarios where we need to display or process a StoryPart with its related entities.
///
///  Updated By:    Jamie Coker on 2025-11-30
///  Update Notes:  Added a method to fetch the *latest* StoryPart (highest partOrder)
///                 for enforcing turn-based story writing. This prevents a user from
///                 submitting two consecutive StoryParts.
/// ==========================================



import com.loreweave.loreweave.model.StoryPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoryPartRepository extends JpaRepository<StoryPart, Long> {
    @Query("SELECT sp FROM StoryPart sp WHERE sp.story.id = :storyId ORDER BY sp.createdAt DESC")
    List<StoryPart> findLatestStoryPartsForStory(@Param("storyId") Long storyId);

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

    // New method to find the maximum partOrder for a given story
    @Query("""
            select coalesce(max(sp.partOrder), 0) 
            from StoryPart sp 
            where sp.story.id = :storyId
            """)
    Integer findMaxPartOrderForStory(@Param("storyId") Long storyId);

    // New method to fetch a StoryPart by its ID along with its Contributor and User
    @Query("""
                select sp
                from StoryPart sp
                left join fetch sp.contributor c
                left join fetch c.user u
                left join fetch sp.story s
                where sp.id = :id
            """)
    Optional<StoryPart> findByIdWithContributorUserAndStory(@Param("id") Long id);

    // ==========================================
// NEW METHOD — last part in a story
// ==========================================
    Optional<StoryPart> findTopByStoryIdOrderByPartOrderDesc(Long storyId);
}