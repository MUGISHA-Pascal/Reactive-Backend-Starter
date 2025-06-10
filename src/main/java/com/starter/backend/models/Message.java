package com.starter.backend.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table("messages")
public class Message {
    @Id
    private UUID id;
    private String message;
    private String sender;
    private String receiver;
    private String is_read;
}
