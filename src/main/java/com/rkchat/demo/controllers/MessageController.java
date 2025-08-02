package com.rkchat.demo.controllers;

import com.rkchat.demo.User;
import com.rkchat.demo.dto.ChatMessage;
import com.rkchat.demo.enums.MessageType;
import com.rkchat.demo.models.Message;
import com.rkchat.demo.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class MessageController {


    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;




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
