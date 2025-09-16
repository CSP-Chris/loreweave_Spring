/// ==========================================
/// File Name:    CustomUserDetailsService.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Loads user details from the database for authentication
/// Updated By:
/// Updated By:
/// ==========================================
package com.loreweave.loreweave.security.service;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // ✅ Wrap your entity into a Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_USER")   // or map roles from your User entity
                .build();
    }
}
