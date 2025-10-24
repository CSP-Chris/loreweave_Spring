/// ==========================================
/// File Name:    NotificationView.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-22
/// Purpose:      Data Transfer Object for notifications in WebSocket communication.
/// 
///  Updated By:   
///  Update Notes:  
/// ==========================================
package com.loreweave.loreweave.dto.ws;

public record NotificationView(
    String text,
    String from,
    String createdAt
) {}
