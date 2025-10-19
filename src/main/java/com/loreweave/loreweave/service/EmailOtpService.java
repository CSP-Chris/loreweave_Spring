/// ==========================================
/// File Name:    EmailOtpService.java
/// Created By:   Jamie Coker
/// Created On:   2025-10-19
/// Purpose:      Combined service for sending email OTPs and verifying them.
///                Used during registration to confirm user email legitimacy.
/// ==========================================

package com.loreweave.loreweave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailOtpService {

    private final JavaMailSender mailSender;
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    public EmailOtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static class OtpEntry {
        String otp;
        LocalDateTime expiry;
        OtpEntry(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }

    // Generate OTP and send via email
    public void sendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Loreweave Email Verification Code");
        message.setText("Your Loreweave verification code is: " + otp + "\n\nThis code expires in 5 minutes.");
        mailSender.send(message);
    }

    // Verify OTP for email
    public boolean verifyOtp(String email, String otpInput) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null) return false;
        if (entry.expiry.isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            return false;
        }
        boolean valid = entry.otp.equals(otpInput);
        if (valid) otpStorage.remove(email);
        return valid;
    }
}
