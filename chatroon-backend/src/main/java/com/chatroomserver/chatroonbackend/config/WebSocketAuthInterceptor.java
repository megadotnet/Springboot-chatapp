package com.chatroomserver.chatroonbackend.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collections;
import java.util.Map;


@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        
        // 从请求中获取token（示例使用Authorization头）
        String token = request.getHeaders().getFirst("Authorization");
        
        if (!validateToken(token)) {
            throw new AuthenticationCredentialsNotFoundException("未授权的连接请求");
        }
        
        // 将用户信息存入attributes供后续使用
        attributes.put("user", extractUserFromToken(token));
        return true;
    }

    private boolean validateToken(String token) {
        // 实现具体的Token验证逻辑（如JWT验证）
        return token != null && token.startsWith("Bearer ");
    }

    private UserDetails extractUserFromToken(String token) {
        // 实现用户信息提取逻辑
        return new User("username", "", Collections.emptyList());
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
