package com.starter.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private MessageType type;
    private long timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
