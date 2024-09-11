package com.chatroomserver.chatroonbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
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

