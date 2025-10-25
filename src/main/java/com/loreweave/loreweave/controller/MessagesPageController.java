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
///  Updated By:    Chris Ennis
///  Update Notes:  Removed the send method to rely on WebSocket handling.
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
}
