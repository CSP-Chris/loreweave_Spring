/// ==========================================
/// File Name:    AuthController.java
/// Created By:   Jamie Coker
/// Created On:   2025-09-15
/// Purpose:      Handles login requests and issues JWT tokens
/// Updated By:
/// Updated By:
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Generate and return token
        return jwtService.generateToken(request.getEmail());
    }

    // ✅ Replaced record with a simple DTO class for Java 8+ compatibility
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}