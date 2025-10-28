package com.loreweave.loreweave.controller;

/// ==========================================
/// File Name:    EmailVerificationController.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-19
/// Purpose:      Handles sending and verifying email OTP codes.
/// Updated By:  Jamie Coker on 10/27/2025
///   Update Notes: Handles OTP validation and updates user to enabled.
/// ==========================================



import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.EmailOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EmailVerificationController {

    private final EmailOtpService emailOtpService;
    private final UserRepository userRepository;

    @Autowired
    public EmailVerificationController(EmailOtpService emailOtpService, UserRepository userRepository) {
        this.emailOtpService = emailOtpService;
        this.userRepository = userRepository;
    }

    /**
     * Step 1: Send OTP (only if email not already used)
     */
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {
        // Check if email already exists in the database
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null && existingUser.isEnabled()) {
            model.addAttribute("error", "This email is already registered and verified. Please log in.");
            return "register"; // Redirect or render the registration page with error
        }

        // If user exists but is not verified, allow resending OTP
        if (existingUser != null && !existingUser.isEnabled()) {
            emailOtpService.sendOtp(email);
            model.addAttribute("email", email);
            model.addAttribute("message", "Verification code resent! Check your email.");
            return "verify-email";
        }

        // If it's a brand-new email, send OTP
        emailOtpService.sendOtp(email);
        model.addAttribute("email", email);
        model.addAttribute("message", "Verification code sent! Please check your email.");
        return "verify-email";
    }

    /**
     * Step 2: Display the OTP verification page
     */
    @GetMapping("/verify-email")
    public String showVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

    /**
     * Step 3: Verify the OTP and activate the account
     */
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, @RequestParam String otp, Model model) {
        boolean valid = emailOtpService.verifyOtp(email, otp);
        if (valid) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setEnabled(true);
                userRepository.save(user);
            }
            model.addAttribute("message", "Email verified successfully! You can now log in.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            model.addAttribute("email", email);
            return "verify-email";
        }
    }
}
