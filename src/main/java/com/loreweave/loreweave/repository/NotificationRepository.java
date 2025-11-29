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
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Added an update method so the user can mark all 
//                  unread notifications 
/// ==========================================

package com.loreweave.loreweave.repository;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsRead(User user, boolean isRead);

    // Custom method to count unread notifications for a user
    long countByUserAndIsReadFalse(User user);

    // Mark all unread notifications as read for a given user
    @Modifying
    @Transactional
    @Query("""
            update Notification n 
            set n.isRead = true 
            where n.user = :user and n.isRead = false
        """)
    int markAllReadByUser(@Param("user") User user);
}
