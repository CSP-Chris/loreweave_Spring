/// ==========================================
/// File Name:    Notification.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      JPA file that creates the Notification entity
/// Update History:
///             Updated By: Wyatt Bechtle
///         Update Details: Added senderUsername field to track who sent the notification
/// ==========================================

package com.loreweave.loreweave.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String message;

    private String link;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Notification(User recipient, String content, Object message) {}

    public Notification(User user, User sender, String message, String link) {
        this.user = user;
        this.sender = sender;
        this.message = message;
        this.link = link;
        this.createdAt = LocalDateTime.now();
    }

    public Notification() {

    }
}
