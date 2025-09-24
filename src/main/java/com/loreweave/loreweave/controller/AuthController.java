/// ==========================================
/// File Name:    AuthController.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Handles login requests and issues JWT tokens
///  Updated By: Jamie Coker on 9/21/2025
///  Update Notes: Integrated password encoding + JWT response.
///         Combined Thymeleaf views + REST API in one controller.
/// Jamie Coker on 9/25/2025
///  Update Notes: Removed JWT handling, simplified controller to only
///                handle user registration. Login/logout now managed
///                by Spring Security with Thymeleaf.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/auth")
public class AuthController {

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

/**
 * Handles user registration.
 * Encodes the password and saves the user to the database.
 * Redirects to login page on success.
 */
@PostMapping("/register")
public String register(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return "redirect:/loginBSF"; // after successful registration
}
}

