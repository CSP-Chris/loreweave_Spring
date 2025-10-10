/// ==========================================
/// File Name:    Notification.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      JPA file that creates the Notification entity
/// Update History:
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

    @Column(nullable = false)
    private String message;

    private String link;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Notification() {}

    public Notification(User user, String message, String link) {
        this.user = user;
        this.message = message;
        this.link = link;
        this.createdAt = LocalDateTime.now();
    }
}
