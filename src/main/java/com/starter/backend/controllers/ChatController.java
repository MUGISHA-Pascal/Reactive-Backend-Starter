package com.starter.backend.controllers;

import com.starter.backend.models.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class ChatController {

    @GetMapping("/api/chat/status")
    @ResponseBody
    public Mono<String> getStatus() {
        return Mono.just("Chat service is running");
    }
}
