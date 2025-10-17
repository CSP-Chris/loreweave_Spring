/// ==========================================
/// File Name:    CharacterRepository.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-02
/// Purpose:      Repository interface for Character entity
/// Update History: 
///             Updated By: Wyatt Bechtle
///     Update discription: Fixed Javadoc for findByUser method
/// 
///             Updated By: Wyatt Bechtle
///     Update discription: Added incrementLorePoints method to adjust lore points.
///                         Added sumVotesForCharacter method to total votes for a character.
///
/// ==========================================
package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    /**
     * Find a user by username.
     * @param user the user associated with the character
     * @return Optional containing User if found
     */
    Optional<Character> findByUser(User user);
    /**
     * Increment the lore points of a character by a specified amount.
     * @param characterId the ID of the character
     * @param delta the amount to increment (can be negative)
     */
    @Modifying
    @Transactional
    @Query("""
            update Character c 
            set c.lorePoints = c.lorePoints + :delta 
            where c.id = :id
            """)
    void incrementLorePoints(@Param("id") Long characterId, @Param("delta") int delta);
    /**
     * Sum all vote amounts for story parts contributed by a specific character.
     * @param characterId the ID of the character
     * @return the total sum of votes
     */
    @Query("""
    select coalesce(sum(lv.amount), 0)
    from LoreVote lv
    join lv.storyPart sp
    where sp.contributor.id = :characterId
    """)
    int sumVotesForCharacter(@Param("characterId") Long characterId);

}
