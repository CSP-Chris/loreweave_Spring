/// ==========================================
/// File Name:    CharacterRepository.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-02
/// Purpose:      Repository interface for Character entity
/// Update History:
///
/// ==========================================
package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    /**
     * Find a user by username.
     * @param username the username to look up
     * @return Optional containing User if found
     */
    Optional<Character> findByUser(User user);
}
