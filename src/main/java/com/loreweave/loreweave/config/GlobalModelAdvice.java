/// ==========================================
/// File Name:    GlobalModelAdvice.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-11
/// Purpose:      Adds global model attributes to all controllers
///  Updated By: 
///  Update Notes:
///  
/// ==========================================
package com.loreweave.loreweave.config;

import com.loreweave.loreweave.repository.UserRepository;
import com.loreweave.loreweave.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Adds attributes to the model for all controllers
@ControllerAdvice 
public class GlobalModelAdvice {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public GlobalModelAdvice(NotificationService notificationService,
                             UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // Adds unread notification count to the model
    @ModelAttribute("unreadCount")
    public int populateUnreadCount(Authentication auth) {

        // If user is not authenticated, return 0
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return 0;
        }
        // Fetch user and count unread notifications
        return userRepository.findByUsername(auth.getName())
                .map(user -> notificationService.getUnreadNotifications(user).size())
                .orElse(0);
    }
}
