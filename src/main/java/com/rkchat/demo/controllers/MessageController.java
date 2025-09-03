package com.rkchat.demo.controllers;

import com.rkchat.demo.models.User;

import com.rkchat.demo.dto.MessageDTO;
import com.rkchat.demo.dto.TypingStatus;
import com.rkchat.demo.enums.MessageType;
import com.rkchat.demo.models.Message;
import com.rkchat.demo.repositories.MessageRepository;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        List<MessageDTO> messageDTOs = messages.stream()
                .map(message -> {
                    MessageDTO dto = MessageDTO.builder()
                            .id(message.getId())
                            .senderId(message.getSender().getId())
                            .recipientId(message.getRecipient().getId())
                            .content(message.getContent())
                            .timestamp(message.getTimestamp())
                            .messageType(message.getMessageType().name())
                            .fileName(message.getFileName()) // This is where fileName is included
                            .build();

                    return dto;
                }).collect(Collectors.toList());


        return messageDTOs;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO chatMessage, Principal principal) {


        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found in WebSocket session"));

        User recipient = null;
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
                .fileName(chatMessage.getFileName())
                .build();

        // Save the message and get the saved entity with generated ID
        Message savedMessage = messageService.saveMessage(message);

        // Update the DTO with the generated ID before sending via WebSocket
        MessageDTO responseDTO = MessageDTO.builder()
                .id(savedMessage.getId())
                .senderId(savedMessage.getSender().getId())
                .recipientId(savedMessage.getRecipient() != null ? savedMessage.getRecipient().getId() : null)
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .messageType(savedMessage.getMessageType().name())
                .fileName(savedMessage.getFileName())
                .build();

        // Send the updated DTO with the generated ID
        if (chatMessage.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId().toString(),
                    "/queue/messages",
                    responseDTO
            );
            messagingTemplate.convertAndSendToUser(
                    sender.getId().toString(),
                    "/queue/messages",
                    responseDTO
            );
        } else {
            messagingTemplate.convertAndSend("/topic/chat", responseDTO);
        }
    }

    @MessageMapping("/chat.typing")
    public void sendTypingStatus(@Payload TypingStatus typingStatus) {
        if (typingStatus.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    typingStatus.getRecipientId().toString(),
                    "/queue/typing",
                    typingStatus
            );
        }
    }

    @DeleteMapping("/history/{user1Id}/{user2Id}")
    public void clearChatHistory(@PathVariable Long user1Id, @PathVariable Long user2Id) {

        messageService.clearChatHistory(user1Id, user2Id);
    }
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Long messageId,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get the current user
            User currentUser = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Attempt to delete the message
            boolean deleted = messageService.deleteMessage(messageId, currentUser.getId());

            if (deleted) {
                response.put("success", true);
                response.put("messageId", messageId);
                response.put("message", "Message deleted successfully");

                messagingTemplate.convertAndSend(
                        "/topic/delete",
                        Map.of("id", messageId)
                );

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Message not found or you don't have permission to delete it");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (UsernameNotFoundException e) {
            response.put("success", false);
            response.put("error", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to delete message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}