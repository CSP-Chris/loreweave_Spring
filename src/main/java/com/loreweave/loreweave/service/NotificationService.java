/// ==========================================
/// File Name:    NotificationService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for notification-related business logic
/// Update History:
/// ==========================================

package com.loreweave.loreweave.service;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsRead(user, false);
    }
}
