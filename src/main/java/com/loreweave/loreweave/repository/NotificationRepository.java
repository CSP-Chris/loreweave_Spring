/// ==========================================
/// File Name:    NotificationRepository.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      JPA repository for the Notification entity
/// Update History:
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added method to count unread notifications for a user
///  
/// ==========================================

package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsRead(User user, boolean isRead);

    // Custom method to count unread notifications for a user
    long countByUserAndIsReadFalse(User user);
}
