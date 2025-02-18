package com.chatroomserver.chatroonbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

// Spring WebSocket配置类
@Configuration
// 启用WebSocket消息代理
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {




    /**
     * 注册STOMP端点
     * 此方法用于设置WebSocket的连接端点，并允许跨域访问
     * 使用SockJS协议作为WebSocket的后备方案，以确保在不支持WebSocket时依然可以通信
     */
    // 添加认证拦截器
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketAuthInterceptor()) // 添加认证拦截器
                .withSockJS();
    }


    // 添加消息通道拦截
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 验证连接时的认证信息
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (!validateToken(token)) {
                        throw new AuthenticationCredentialsNotFoundException("无效的认证凭证");
                    }
                }
                return message;
            }
        });
    }

    private boolean validateToken(String token) {
        // 实现具体的Token验证逻辑（如JWT验证）
        return token != null && token.startsWith("Bearer ");
    }

    /**
     * 配置消息代理
     * 设置应用程序的目的地前缀，以及启用简单的消息代理
     * 通过设置前缀，确定了消息代理的路由规则，以及用户特定的目的地前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置应用程序的消息目的地前缀
        registry.setApplicationDestinationPrefixes("/app");

        // 启用简单消息代理，并设置目的地前缀
        registry.enableSimpleBroker("/chatroom", "/user");

        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 配置WebSocket传输层设置
     * 设置消息发送的时间限制、发送缓冲区大小限制和消息大小限制
     * 这些限制是为了确保WebSocket连接的稳定性和性能
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(60 * 1000)
                .setSendBufferSizeLimit(50 * 1024 * 1024)
                .setMessageSizeLimit(50 * 1024 * 1024);
    }
}

