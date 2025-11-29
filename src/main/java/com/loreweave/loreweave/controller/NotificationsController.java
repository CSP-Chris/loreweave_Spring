/// ==========================================
/// File Name:    NotificationsController.java
/// Created By:   Wyatt Bechtle
/// Created On:   11/20/25
/// Purpose:      Controller for handling notification-related endpoints
///
/// Update History:
///   Updated By:   Wyatt Bechtle
///   Update Notes: Added controller and handlers to support client dropdown UI.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.NotificationRepository;
import com.loreweave.loreweave.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class NotificationsController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationsController(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Return unread notifications for the authenticated user as JSON
    @GetMapping("/notifications/unread")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Map<String,Object>> unread(Authentication auth) {

        // If there's no authentication, return an empty list (client will show no unread items)
        if (auth == null || auth.getName() == null) return List.of();

        // Resolve the current user entity
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();

        // Query unread notifications (readOnly transactional to avoid lazy-init problems when serializing)
        List<Notification> unread = notificationRepository.findByUserAndIsRead(current, false);

        // Map Notification -> Map used by the client. 
        return unread.stream().map(n -> {
            Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", n.getId());
            m.put("message", n.getMessage());
            m.put("link", n.getLink());
            m.put("from", n.getSender() != null ? n.getSender().getUsername() : null);
            m.put("createdAt", n.getCreatedAt().toString());
            return m;
        }).collect(Collectors.toList());
    }

    // Mark a notification as read 
    @GetMapping("/notifications/{id}/read")
    @Transactional
    public ResponseEntity<Void> markRead(@PathVariable("id") Long id, Authentication auth) {

        // Find the notification and guard that the authenticated user is the recipient before mutating
        var notifOpt = notificationRepository.findById(id);
        if (notifOpt.isEmpty()) return ResponseEntity.notFound().build();
        Notification n = notifOpt.get();

        // ensure current user is recipient
        if (auth == null || auth.getName() == null) return ResponseEntity.status(403).build();
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (n.getUser() == null || !n.getUser().getId().equals(current.getId())) return ResponseEntity.status(403).build();

        // Mark and persist the single notification
        n.setRead(true);
        notificationRepository.save(n);
        return ResponseEntity.ok().build();
    }

    // View a notification: mark read and redirect to its link (or to /messages)
    @GetMapping("/notifications/{id}/view")
    @Transactional
    public String viewNotification(@PathVariable("id") Long id, Authentication auth) {

        // Mark the notification read and redirect the user to the notification's link.
        var notifOpt = notificationRepository.findById(id);
        if (notifOpt.isEmpty()) return "redirect:/messages";
        Notification n = notifOpt.get();
        if (auth == null || auth.getName() == null) return "redirect:/login";
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();

        // Ensure only the recipient can view/activate the notification
        if (n.getUser() == null || !n.getUser().getId().equals(current.getId())) return "redirect:/messages";
        n.setRead(true);
        notificationRepository.save(n);

        // Redirect to the stored link if present, otherwise to a sane fallback
        String link = n.getLink();
        if (link == null || link.isBlank()) return "redirect:/messages";
        return "redirect:" + link;
    }

    // Mark all unread notifications for the authenticated user as read
    @PostMapping("/notifications/mark-all-read")
    @Transactional
    public ResponseEntity<Void> markAllRead(Authentication auth) {

        // Guard for authenticated users only
        if (auth == null || auth.getName() == null) return ResponseEntity.status(403).build();
        User current = userRepository.findByUsername(auth.getName()).orElseThrow();

        // Use a single bulk update query to mark unread notifications as read for the current user
        notificationRepository.markAllReadByUser(current);
        return ResponseEntity.ok().build();
    }
}
