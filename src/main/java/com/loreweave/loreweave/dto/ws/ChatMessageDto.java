/// ==========================================
/// File Name:    ChatMessageDto.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-22
/// Purpose:      Data Transfer Object for chat messages in WebSocket communication.
/// 
///  Updated By:   
///  Update Notes:  
/// ==========================================
package com.loreweave.loreweave.dto.ws;
 
public record ChatMessageDto(
    String text,
    String to
) {}
