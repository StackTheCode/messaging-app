package com.rkchat.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private  Long senderId;
    private  Long recipientId;
    private  String content;
    private LocalDateTime timestamp;
    private  String messageType;
}
