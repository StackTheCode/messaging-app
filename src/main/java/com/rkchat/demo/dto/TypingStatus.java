package com.rkchat.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TypingStatus {
    private  Long senderId;
    private Long recipientId;
    private  boolean typing;
}
