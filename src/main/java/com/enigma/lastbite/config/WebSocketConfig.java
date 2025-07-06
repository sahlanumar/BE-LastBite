package com.enigma.lastbite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix untuk topic yang akan di-subscribe oleh client
        // Server akan mengirim pesan ke destinasi yang diawali dengan /topic
        registry.enableSimpleBroker("/topic");

        // Prefix untuk destinasi pesan dari client ke server
        // Contoh: client mengirim pesan ke /app/chat
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint yang akan digunakan client untuk koneksi WebSocket
        // '/ws' adalah endpoint koneksi. withSockJS() untuk fallback jika WebSocket tidak didukung
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}