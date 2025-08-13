package com.rkchat.demo.dto;

import com.rkchat.demo.enums.MessageType;
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
    private String filename;
    private MessageType type;
    private LocalDateTime timestamp;
    private  String messageType;
}
