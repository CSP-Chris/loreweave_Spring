/// ==========================================
/// File Name:    AuthController.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Handles login requests and issues JWT tokens
///  Updated By: Jamie Coker on 9/21/2025
///  Update Notes: Integrated password encoding + JWT response.
/// Updated By: Wyatt Bechtle - 9/21/2025
///             Updated the mapping to register
///                 Added try catch for failure registering
///                 Updated to accept data from Thymeleaf
///             Updated restController to Controller for Thymeleaf models use
///             Removed login mapping
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.loreweave.loreweave.model.User;
import java.util.Map;
import org.springframework.ui.Model;




@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register new user
    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password
            userRepository.save(user);
            return "redirect:/loginBSF";
        }
        catch (Exception exception) {
            model.addAttribute("user", user);
            model.addAttribute("error", exception.getMessage());
            return "register";
        }
        
    }

    //  Provide model for Thymeleaf register form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // resolves to register.html
    }

    // Replaced record with a simple DTO class for Java 8+ compatibility
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }


}

