package com.rkchat.demo.controllers;


import com.rkchat.demo.models.User;
import com.rkchat.demo.repositories.UserRepository;
import com.rkchat.demo.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

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

}
