package com.rkchat.demo.models;

import com.rkchat.demo.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id",nullable = false)
    private User recipient;

    private  String content;

    @Column(name = "file_name")
    private String fileName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(name = "is_read", nullable = false)
    private  boolean isRead;


}