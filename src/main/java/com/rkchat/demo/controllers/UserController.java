package com.rkchat.demo.controllers;

import com.rkchat.demo.models.User;
import com.rkchat.demo.repositories.MessageRepository;
import com.rkchat.demo.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public UserController(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/search")
    public  List<User> searchUsers(@RequestParam(required = false) String query) {
        if ( query !=null && !query.trim().isEmpty()) {
            return  userRepository.findByUsernameContainingIgnoreCase(query);
        }
        return userRepository.findAll();
    }

    @GetMapping("/conversations/{userId}")
    public List<User> getConversationPartners(@PathVariable Long userId) {
        List<Long> partnerIds = messageRepository.findConversationPartnerIds(userId);

        return userRepository.findAllById(partnerIds);

    }
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    }
