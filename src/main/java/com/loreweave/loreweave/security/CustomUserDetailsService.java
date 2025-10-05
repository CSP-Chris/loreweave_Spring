/// ==========================================
/// File Name:    CustomUserDetailsService.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Loads user details from the database for authentication
/// Updated By:   Jamie Coker
/// Updated On:   2025-09-16
/// Update Note:  Added Spring Security UserDetails builder with
///               authorities to resolve “cannot find symbol” compile errors.
/// Updated By:   Jamie Coker on 2025-09-23
///  Update Notes: integrated with session-based
///                authentication instead of JWT.
/// Updated By:   Wyatt on 2025-10-04
///  Update Notes: Moved file into a security package for Spring Boot to find it.
/// ==========================================
package com.loreweave.loreweave.security;

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

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER") 
                .build();
    }
}
