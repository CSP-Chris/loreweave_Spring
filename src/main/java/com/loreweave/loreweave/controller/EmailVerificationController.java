/// ==========================================
/// File Name:    EmailVerificationController.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-19
/// Purpose:      Handles sending and verifying email OTP codes.
/// ==========================================

package com.loreweave.loreweave.controller;

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

    @GetMapping("/verify-email")
    public String showVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

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
