package com.rkchat.demo.controllers;


import com.rkchat.demo.models.User;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import  java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    private  final JwtService jwtService;
    private  final UserRepository userRepository;


    @Override
    public  void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication)
        throws IOException{
        OAuth2User oAuth2User  = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getName();
        String googleId = oAuth2User.getAttribute("sub");
        String profilePictureUrl = oAuth2User.getAttribute("picture");

        User user = findOrCreateUser(email, name, googleId, profilePictureUrl);

        String token =jwtService.generateToken(user.getUsername());


        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .queryParam("userId", user.getId())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request,response,redirectUrl);

    }
    private User findOrCreateUser(String email,String name,String googleId,String profilePictureUrl) {
        Optional<User> existingUserByGoogleId = userRepository.findByGoogleId(googleId);
        if(existingUserByGoogleId.isPresent()){
            return  existingUserByGoogleId.get();
        }
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if(existingUserByEmail.isPresent()){
            User user = existingUserByEmail.get();

            user.setGoogleId(googleId);
            user.setProfilePictureUrl(profilePictureUrl);
            return  userRepository.save(user);
        }
        User newUser = new User();
        newUser.setUsername(generateUniqueUsername(name,email));
        newUser.setEmail(email);
        newUser.setRole("USER");
        newUser.setGoogleId(googleId);
        newUser.setProvider(User.AuthProvider.GOOGLE);
        newUser.setProfilePictureUrl(profilePictureUrl);


    return userRepository.save(newUser);


    }

    private String generateUniqueUsername(String name, String email) {
        // Try to use name first
        String baseUsername = name.toLowerCase().replaceAll("\\s+", "");

        // If name is not available, use email prefix
        if (baseUsername.isEmpty()) {
            baseUsername = email.split("@")[0];
        }

        String username = baseUsername;
        int counter = 1;

        // Ensure username is unique
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
    
}
