package com.rkchat.demo.controllers;

import com.rkchat.demo.User;
import com.rkchat.demo.dto.AuthResponse;
import com.rkchat.demo.dto.LoginRequest;
import com.rkchat.demo.dto.RegisterRequest;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.CustomUserDetailsService;
import com.rkchat.demo.services.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);

            User userEntity = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Long userId = userEntity.getId();

            return ResponseEntity.ok(new AuthResponse(token,userId));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    // Optional: register endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(("Username already exists"));
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail((request.getEmail()));
        String encodedPassword = passwordEncoder.encode(request.getPassword());
       newUser.setPassword(encodedPassword);
        newUser.setRole("USER");
        userRepository.save(newUser);
        // Save user to database using repository
        return ResponseEntity.ok("User registered");
    }
}