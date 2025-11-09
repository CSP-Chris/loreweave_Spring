
package com.loreweave.loreweave.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(EmailOtpService.class);

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
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        otpStorage.put(email, new OtpEntry(otp, expiryTime));
        log.info("OTP sent to {}. OTP: {}. Expires at: {}", email, otp, expiryTime);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Loreweave Email Verification Code");
        message.setText("Your Loreweave verification code is: " + otp + "\n\nThis code expires in 5 minutes.");
        mailSender.send(message);
    }

    // Verify OTP for email
    public boolean verifyOtp(String email, String otpInput) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null) {
            log.warn("Verification attempt for non-existent or already used OTP for email: {}", email);
            return false;
        }
        log.info("Verification attempt for {}. Input OTP: {}. Stored OTP: {}. Expiry: {}. Current time: {}",
                 email, otpInput, entry.otp, entry.expiry, LocalDateTime.now());

        if (entry.expiry.isBefore(LocalDateTime.now())) {
            log.warn("OTP for {} expired at {}. Current time: {}", email, entry.expiry, LocalDateTime.now());
            otpStorage.remove(email);
            return false;
        }
        boolean valid = entry.otp.equals(otpInput);
        if (valid) {
            log.info("OTP for {} successfully verified.", email);
            otpStorage.remove(email);
        } else {
            log.warn("Invalid OTP {} for email {}. Stored OTP: {}", otpInput, email, entry.otp);
        }
        return valid;
    }
}
