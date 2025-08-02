package com.rkchat.demo.interceptors;

import com.rkchat.demo.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor  implements HandshakeInterceptor {
    private final JwtService jwtService;

    @Autowired
    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpRequest = serverHttpRequest.getServletRequest();
            String token = httpRequest.getParameter("token"); // ✅ key fix here
            if (token != null) {
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    attributes.put("username", username);
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
