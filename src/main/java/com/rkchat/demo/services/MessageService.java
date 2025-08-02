package com.rkchat.demo.services;

import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private  final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    public Message saveMessage(Message message){
        message.setTimestamp(LocalDateTime.now());
        return  messageRepository.save(message);
    }
}
