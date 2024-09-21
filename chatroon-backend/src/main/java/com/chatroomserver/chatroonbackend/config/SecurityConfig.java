package com.chatroomserver.chatroonbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.Duration;

@Configuration // 表明这是一个配置类，用于定义Spring Boot应用的各种配置。
public class SecurityConfig {

    /**
     * 创建一个RateLimitFilter Bean，用于配置请求频率限制。
     *
     * @return 返回一个新的RateLimitFilter实例。
     */
    @Bean // 表明这是一个Bean方法，用于创建并注册一个Bean到Spring容器中。
    public RateLimitFilter rateLimitFilter() {
        // 返回一个新的RateLimitFilter实例，设置每分钟最多10个请求，桶容量为10
        return new RateLimitFilter(10, 10, Duration.ofMinutes(1));
    }

}
