package com.rkchat.demo.services;

import com.rkchat.demo.models.User;
import com.rkchat.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private  final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
       return   org.springframework.security.core.userdetails.User.builder()
               .username(user.getUsername())
                       .password(user.getPassword())
                               .roles(user.getRole())
                                       .build();


    }
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
    
}
