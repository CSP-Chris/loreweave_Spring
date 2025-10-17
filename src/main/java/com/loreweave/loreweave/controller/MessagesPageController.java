/// ==========================================
/// File Name:    MessagesPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-10
/// Purpose:      Controller for handling messaging functionality
///               including viewing and sending messages.
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Removed unnecessary import.
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessagesPageController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public MessagesPageController(NotificationService notificationService,
                                  UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }
    
    // Display the messages page
    @GetMapping("/messages")
    public String messages(Authentication auth, Model model) {
        // Fetch the logged-in user
        User current = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
        model.addAttribute("conversations", null);
        model.addAttribute("activeConversation", null);
        model.addAttribute("messages", notificationService.getUnreadNotifications(current));
        return "messages";
    }
    // Handle sending a new message
    @PostMapping("/messages/send")
    public String send(Authentication auth,
                       @RequestParam("to") String toUsername,
                       @RequestParam("content") String content) {
        // Fetch recipient users
        User recipient = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + toUsername));
        // Create and save the notification
        Notification n = new Notification(recipient, content, null);
        notificationService.createNotification(n);

        return "redirect:/messages";
    }
}
