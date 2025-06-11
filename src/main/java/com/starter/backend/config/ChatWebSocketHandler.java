package com.starter.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.backend.models.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Sinks.Many<Message> sink;
    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.put(session.getId(), session);

        Mono<Void> input = session.receive()
            .doOnNext(message -> {
                try {
                    Message chatMessage = objectMapper.readValue(message.getPayloadAsText(), Message.class);
                    sink.tryEmitNext(chatMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
            .then();

        Mono<Void> output = session.send(
            sink.asFlux()
                .map(message -> {
                    try {
                        return objectMapper.writeValueAsString(message);
                    } catch (Exception e) {
                        return "Error serializing message";
                    }
                })
                .map(session::textMessage)
        );

        return Mono.zip(input, output).then()
            .doFinally(signalType -> sessions.remove(session.getId()));
    }
} 