package com.rkchat.demo.services;

import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private  final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Message saveMessage(Message message){
        message.setTimestamp(LocalDateTime.now());
        return  messageRepository.save(message);
    }
    @Transactional
    public void clearChatHistory(Long user1Id,Long user2Id){
        messageRepository.deleteMessagesBetweenUsers(user1Id,user2Id);
    }
}
