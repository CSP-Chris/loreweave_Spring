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
///  Updated By:   Chris Ennis
///  Update Notes: Added GET mapping to display registration page and provide user object to the model.
/// 
/// Updated By:   Wyatt Bechtle
///  Update Notes: changed loginBSF -> login
///  Updated By:  Jamie Coker on 10/19/2025
///  Update Notes: Integrated email OTP verification flow.
///                - Injected EmailOtpService
///                - Sends OTP after registration
///                - Disables user until verified
/// Updated By:  Jamie Coker on 10/27/2025
///  Update Notes: Added email check + OTP resend for existing/unverified users.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.EmailOtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailOtpService emailOtpService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailOtpService emailOtpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailOtpService = emailOtpService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Handles new user registration.
     * - Checks if email already exists
     * - Creates user as disabled until verified
     * - Sends OTP to verify email
     */
    @PostMapping("/register")
    public String register(User user, Model model) {

        // Step 1: Check if email already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {

            // Case 1: User already verified
            if (existingUser.isEnabled()) {
                model.addAttribute("error", "Email already registered. Please log in instead.");
                return "register";
            }

            // Case 2: User exists but not yet verified → resend OTP
            emailOtpService.sendOtp(existingUser.getEmail());
            model.addAttribute("message", "Verification email resent! Please check your inbox.");
            return "verify-email";
        }

        // Step 2: Register new user (disabled until verified)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        userRepository.save(user);

        // Step 3: Send OTP to email
        emailOtpService.sendOtp(user.getEmail());

        // Step 4: Redirect to verification page
        return "redirect:/verify-email?email=" + user.getEmail();
    }
}
