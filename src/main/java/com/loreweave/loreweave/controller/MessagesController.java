/// ==========================================
/// File Name:    MessageController.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Controller for handling messages as a REST fallback
///  (This system can be used depending on time for the final updates)
/// Update History:
///       Updated By: Wyatt Bechtle
///   Update Details: Refactored to MessagesController.java and added WebSocket support,
///                   along with private messaging and notification persistence.
///       Updated By: Chris Ennis
///   Update Details: Corrected Notification object creation.
/// 
///       Updated By: Wyatt Bechtle
///   Update Details: Added a link to messages in the notification so users can
///                   open the messages UI from the notification.
/// ==========================================

package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.dto.ws.ChatMessageDto;
import com.loreweave.loreweave.dto.ws.NotificationView;
import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MessagesController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MessagesController(NotificationService notificationService,
                              SimpMessagingTemplate simpMessagingTemplate,
                              UserRepository userRepository) {
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    // --- WebSocket: broadcast to all subscribers ---
    // Client sends to /app/application with { text: "..." }
    // Server broadcasts to /topic/messages
    // Note: Authentication is optional here; system messages can be sent unauthenticated
    // The 'from' field is set to "system" if unauthenticated
    // Does not save to DB
    @MessageMapping("/application")
    public void broadcast(@Payload ChatMessageDto dto, Authentication auth) {
        final String from = auth != null ? auth.getName() : "system";
        simpMessagingTemplate.convertAndSend(
            "/topic/messages",
            new NotificationView(dto.text(), from, LocalDateTime.now().toString())
        );
    }

    // --- WebSocket: send private message ---
    // Client sends to /app/private with { to: "username", text: "..." }
    // Server sends to /user/{username}/queue/notifications
    // Requires authentication
    // Saves notification to DB
    // The 'to' field must be a valid username
    // The 'from' field is set to the authenticated user's username
    @MessageMapping("/private")
    public void sendPrivate(@Payload ChatMessageDto dto, Authentication auth) {

        // Ensure authenticated
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated WebSocket message");
        }

        // Lookup sender and recipient
        User sender = userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new IllegalStateException("Sender not found: " + auth.getName()));
        User recipient = userRepository.findByUsername(dto.to())
            .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.to()));

        // Create and save notification to db, attach a link to `/messages` so the recipient can
        // open the messages UI from the notification
        Notification saved = notificationService.createNotification(
            new Notification(recipient, sender, dto.text(), "/messages")
        );

        // Send notification to recipient
        simpMessagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
            "/queue/notifications",
            new NotificationView(saved.getMessage(), sender.getUsername(), saved.getCreatedAt().toString())
        );
    }
    // --- REST: read unread messages ---
    // Client sends GET to /messages/read
    // Requires authentication
    // Returns list of unread notifications for the authenticated user
    // Does not mark notifications as read
    // Response format: List<Notification>
    @GetMapping("/read")
    public ResponseEntity<List<Notification>> readMessages(Authentication auth) {

        // Ensure authenticated
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        // Lookup current user
        User current = userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + auth.getName()));

        // Retrieve unread notifications
        return ResponseEntity.ok(notificationService.getUnreadNotifications(current));
    }
}
