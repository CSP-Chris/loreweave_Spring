/// ==========================================
/// File Name:    NotificationService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for notification-related business logic
/// Update History:
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added method to get count of unread notifications for a user
///  
/// ==========================================
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

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsRead(user, false);
    }
    // New method to get count of unread notifications
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
