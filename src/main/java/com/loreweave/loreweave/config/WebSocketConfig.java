/// ==========================================
/// File Name:    WebSocketConfig.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Configuration for WebSocket and STOMP
/// (This system can be used depending on time for the final updates)
/// Update History:
///      Updated by: Wyatt Bechtle
/// Updated Details: Added user destination prefix configuration.
///                  Added ws endpoint configuration.
///                  Added queue to simple broker.
/// ==========================================

package com.loreweave.loreweave.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws").withSockJS();
    }
}
