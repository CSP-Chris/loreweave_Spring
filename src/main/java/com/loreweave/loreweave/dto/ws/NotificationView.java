/// ==========================================
/// File Name:    NotificationView.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-22
/// Purpose:      Data Transfer Object for notifications in WebSocket communication.
/// 
///  Updated By:   Chris Ennis
///  Update Notes:  Changed 'text' field to 'message' for consistency.
/// ==========================================
package com.loreweave.loreweave.dto.ws;

public record NotificationView(
    String message,
    String from,
    String createdAt
) {}
