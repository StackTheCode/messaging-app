package com.rkchat.demo.controllers;

import com.rkchat.demo.User;

import com.rkchat.demo.dto.MessageDTO;
import com.rkchat.demo.dto.TypingStatus;
import com.rkchat.demo.enums.MessageType;
import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {


    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    @GetMapping("/history/{userId}/{user2Id}")
    public List<MessageDTO> getChatHistory(@PathVariable Long userId, @PathVariable Long user2Id) {
        List<Message> messages = messageRepository.findChatHistory(userId, user2Id);

        return messages.stream()
                .map(message -> MessageDTO.builder()
                        .senderId(message.getSender().getId())
                        .recipientId(message.getRecipient().getId())
                        .content(message.getContent())
                        .timestamp(message.getTimestamp())
                        .messageType(message.getMessageType().name())
                        .build()

                ).collect(Collectors.toList());
    }


    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO chatMessage, Principal principal) {

        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found in WebSocket session"));

        User recipient = chatMessage.getRecipientId() == null ? null : new User(chatMessage.getRecipientId());

        if (chatMessage.getRecipientId() != null) {
            recipient = userRepository.findById(chatMessage.getRecipientId())
                    .orElseThrow(() -> new UsernameNotFoundException("Recipient not found in WebSocket session"));
        }
        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(chatMessage.getContent())
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.valueOf(chatMessage.getMessageType()))
                .isRead(false)
                .build();

        messageService.saveMessage(message);


        if (chatMessage.getRecipientId() != null) {
//            //private chat routing
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId().toString(),
                    "/queue/messages",
                    chatMessage
            );

        } else {
//            // Send to the public topic
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
        }

    }

    @MessageMapping("/chat.typing")
    public void sendTypingStatus(@Payload TypingStatus typingStatus, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found "));

        if (!sender.getId().equals(typingStatus.getSenderId())) {
            throw new UsernameNotFoundException("Sender id mismatch");
        }
        if (typingStatus.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(typingStatus.getRecipientId().toString(),"/queue/typing",typingStatus);
        }

    }
}
