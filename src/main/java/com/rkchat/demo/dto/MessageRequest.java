package com.rkchat.demo.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String recipientUsername;
    private String content;
}
