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
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Implemented conversation listing and viewing logic,
///                 including unread message counts and sorting by latest message.
///                 Controller methods are marked as @Transactional(readOnly = true)
///                 to prevent lazy loading issues when accessing related entities
///                 Implemented Maps for conversation entries to facilitate data 
///                 handling in the view.
///                 Enables user to see messages organized by senders, and when 
///                 clicked, displays the thread.
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.repository.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessagesPageController {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public MessagesPageController(UserRepository userRepository,
                                  NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }
    // Display the messages page
    // Added Transactional for readOnly to avoid lazy loading issues that occurred
    @GetMapping("/messages")
    @Transactional(readOnly = true)
    public String messages(Authentication auth, Model model) {

        // Fetch the logged-in user
        User current = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // Build conversation list with unique partner usernames from all notifications

        // Get all notifications (List)
        var all = notificationRepository.findAll();

        // Initialize conversation map (LinkedHashMap to preserve order)
        // LinkedHashMap: key = partner username, value = Map with conversation details
        var convMap = new java.util.LinkedHashMap<String, java.util.Map<String, Object>>();

        // Populate conversation map
        for (var n : all) {

            // Partner is sender when current is recipient
            if (n.getUser() != null && n.getUser().getId().equals(current.getId())) {

                // Get sender info
                var sender = n.getSender();

                // Add to conv map
                if (sender != null) {

                    // Get sender username
                    var uname = sender.getUsername();

                    // compute if absent, to avoid overwriting existing entries
                    // Map entry structure:
                    // "id": partner username
                    // "otherPartyUsername": partner username
                    // "lastMessageAt": timestamp of latest message in this conversation
                    // "unreadCount": initialized to 0, will be updated later
                    convMap.computeIfAbsent(uname, k -> java.util.Map.of(
                            "id", uname,
                            "otherPartyUsername", uname,
                            "lastMessageAt", n.getCreatedAt(),
                            "unreadCount", 0
                    ));
                }
            }
            // Partner is recipient when current is sender
            if (n.getSender() != null && n.getSender().getId().equals(current.getId())) {

                // Get recipient info
                var recip = n.getUser();

                // Add to conv map
                if (recip != null) {

                    // Get recipient username
                    var uname = recip.getUsername();

                    // Compute if absent, to avoid overwriting existing entries
                    // Map entry structure:
                    // "id": partner username
                    // "otherPartyUsername": partner username
                    // "lastMessageAt": timestamp of latest message in this conversation
                    // "unreadCount": initialized to 0, will be updated later
                    convMap.computeIfAbsent(uname, k -> java.util.Map.of(
                            "id", uname,
                            "otherPartyUsername", uname,
                            "lastMessageAt", n.getCreatedAt(),
                            "unreadCount", 0
                    ));
                }
            }
        }
        // Compute unread counts per partner, current is recipient and isRead==false
        for (var key : convMap.keySet()) {

            // Count unread messages from this partner
            long unreadCount = all.stream()   // stream of all notifications
                    .filter(x -> x.getUser() != null && x.getUser().getId().equals(current.getId())) // current is recipient
                    .filter(x -> x.getSender() != null && key.equals(x.getSender().getUsername()))   // from this partner
                    .filter(x -> !x.isRead()) // unread only
                    .count();

            // Update conv map entry
            // Replace with updated map including unread count, other fields remain the same

            // Get old entry
            var old = convMap.get(key);

            // Create updated entry
            var updated = java.util.Map.of(
                    "id", old.get("id"),
                    "otherPartyUsername", old.get("otherPartyUsername"),
                    "lastMessageAt", old.get("lastMessageAt"),
                    "unreadCount", unreadCount
            );
            convMap.put(key, updated);
        }
        // Add to model and render
        model.addAttribute("conversations", convMap.values());
        model.addAttribute("activeConversation", null);
        model.addAttribute("messages", java.util.Collections.emptyList());
        return "messages";
    }
    // View a specific conversation by partner username
    // Added Transactional for readOnly to avoid lazy loading issues that occurred
    @GetMapping("/messages/{id}")
    @Transactional(readOnly = true)
    public String viewConversation(@PathVariable("id") String id, Authentication auth, Model model) {

        // Fetch the logged-in user
        User current = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // Build conversation list with unique partner usernames from all notifications
        
        // Get all notifications (List)
        var all = notificationRepository.findAll();

        // Initialize conversation map (LinkedHashMap to preserve order)
        // LinkedHashMap: key = partner username, value = Map with conversation details
        var convMap = new java.util.LinkedHashMap<String, java.util.Map<String, Object>>();
        
        // Populate conversation map and build thread with specified partner (List)
        java.util.List<Notification> thread = new java.util.ArrayList<>();

        for (var n : all) {

            // Partner is sender when current is recipient
            if (n.getUser() != null && n.getUser().getId().equals(current.getId())) {

                // Get sender info
                var sender = n.getSender();

                // Add to conv map
                if (sender != null) {

                    // Get sender username
                    var uname = sender.getUsername();

                    // Compute if absent, to avoid overwriting existing entries
                    // Map entry structure:
                    // "id": partner username
                    // "otherPartyUsername": partner username
                    // "lastMessageAt": timestamp of latest message in this conversation
                    // "unreadCount": initialized to 0, will be updated later
                    convMap.computeIfAbsent(uname, k -> java.util.Map.of(
                            "id", uname,
                            "otherPartyUsername", uname,
                            "lastMessageAt", n.getCreatedAt(),
                            "unreadCount", 0
                    ));
                    // add to thread if matches requested id
                    if (uname.equals(id)) thread.add(n);
                }
            }
            // Partner is recipient when current is sender
            if (n.getSender() != null && n.getSender().getId().equals(current.getId())) {

                // Get recipient info
                var recip = n.getUser();

                // Add to conv map
                if (recip != null) {

                    // Get recipient username
                    var uname = recip.getUsername();

                    // Compute if absent, to avoid overwriting existing entries
                    convMap.computeIfAbsent(uname, k -> java.util.Map.of(
                            "id", uname,
                            "otherPartyUsername", uname,
                            "lastMessageAt", n.getCreatedAt(),
                            "unreadCount", 0
                    ));
                    // Add to thread if matches requested id
                    if (uname.equals(id)) thread.add(n);
                }
            }
        }
        // Update unread counts per partner, current is recipient and isRead==false
        for (var key : convMap.keySet()) {
            long unreadCount = all.stream()   // Stream of all notifications
                    .filter(x -> x.getUser() != null && x.getUser().getId().equals(current.getId())) // Current is recipient
                    .filter(x -> x.getSender() != null && key.equals(x.getSender().getUsername()))   // From this partner
                    .filter(x -> !x.isRead()) // Unread only
                    .count();

            // Update conv map entry
            // Replace with updated map including unread count, other fields remain the same

            // Get old entry
            var old = convMap.get(key);

            // Create updated entry
            var updated = java.util.Map.of(
                    "id", old.get("id"),
                    "otherPartyUsername", old.get("otherPartyUsername"),
                    "lastMessageAt", old.get("lastMessageAt"),
                    "unreadCount", unreadCount
            );
            convMap.put(key, updated);
        }

        // Sort thread by createdAt
        thread.sort(java.util.Comparator.comparing(Notification::getCreatedAt));

        // Add to model and render
        model.addAttribute("conversations", convMap.values());
        model.addAttribute("activeConversation", java.util.Map.of("id", id, "otherPartyUsername", id));
        model.addAttribute("messages", thread);
        return "messages";
    }
}
