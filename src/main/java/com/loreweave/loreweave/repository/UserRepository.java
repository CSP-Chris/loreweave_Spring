/// ==========================================
/// File Name:    UserRepository.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Repository interface for User entity
/// Update History:
///  *   2025-09-16 (Jamie Coker)
///  *   - Verified package is com.loreweave.loreweave.repository
///  *   - Ensured correct import of User entity and Optional return
///  *     type for findByUsername so that CustomUserDetailsService
///  *     can use Optional.orElseThrow without errors.
///  *  Verified table name mapping to avoid H2 keyword conflict.
///
/// ==========================================
package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     * @param username the username to look up
     * @return Optional containing User if found
     */
    Optional<User> findByUsername(String username);
}
