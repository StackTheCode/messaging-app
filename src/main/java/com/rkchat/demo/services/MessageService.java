package com.rkchat.demo.services;

import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public void clearChatHistory(Long user1Id, Long user2Id){
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs must not be null");
        }
        messageRepository.deleteMessagesBetweenUsers(user1Id, user2Id);
    }
    public boolean userCanAccessMessage(Long messageId,Long userId){
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }
Message message = messageOpt.get();
        return message.getSender().getId().equals(userId) ||
                (message.getRecipient() != null && message.getRecipient().getId().equals(userId));
    }
}
