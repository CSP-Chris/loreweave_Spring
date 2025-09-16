/// ==========================================
/// File Name:    UserRepository.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Repository interface for User entity
/// Updated By:
/// Updated By:
/// ==========================================
package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}