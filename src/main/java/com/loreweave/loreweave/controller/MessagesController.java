/// ==========================================
/// File Name:    MessageController.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Controller for handling messages as a REST fallback
///  (This system can be used depending on time for the final updates)
/// Update History:
/// ==========================================

package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    private final NotificationService notificationService;

    public MessagesController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendMessage(@RequestBody Notification notification) {
        Notification newNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(newNotification);
    }

    @GetMapping("/read")
    public ResponseEntity<List<Notification>> readMessages(@AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getUnreadNotifications(user);
        return ResponseEntity.ok(notifications);
    }
}
