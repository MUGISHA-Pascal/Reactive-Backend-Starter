package com.starter.backend.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
            .doOnNext(message -> {
                // Handle incoming messages
                String payload = message.getPayloadAsText();
                System.out.println("Received message: " + payload);
            })
            .then();
    }
} 