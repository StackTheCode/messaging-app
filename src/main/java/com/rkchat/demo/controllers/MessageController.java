package com.rkchat.demo.controllers;

import com.rkchat.demo.User;
import com.rkchat.demo.dto.ChatMessage;
import com.rkchat.demo.dto.MessageRequest;
import com.rkchat.demo.enums.MessageType;
import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {


    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    @PostMapping("/send")
public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest) {
    String senderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Sender does not exist"));
        User recipient = userRepository.findByUsername(messageRequest.getRecipientUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Recipient not found"));

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(messageRequest.getContent())
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.CHAT)
                .isRead(false)
                .build();
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/messages",
                message
        );

        return ResponseEntity.ok("Message sent");
    }



    @MessageMapping("/chat.send")
    public  void sendMessage(@Payload ChatMessage chatMessage){

        User sender = new User(chatMessage.getSenderId());
        User recipient = chatMessage.getRecipientId() == null  ? null : new User (chatMessage.getRecipientId());


        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(chatMessage.getContent())
                .timestamp(LocalDateTime.now())
                .messageType(MessageType.valueOf(chatMessage.getMessageType()))
                .isRead(false)
                .build();

        messageService.saveMessage(message);


        if (chatMessage.getRecipientId() == null){
//            public/global message
            messagingTemplate.convertAndSend("/topic/chat",chatMessage);

        }
        else{
//            Private chat
            messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId().toString(),
                    "/queue/messages",chatMessage);
        }
    }
}
