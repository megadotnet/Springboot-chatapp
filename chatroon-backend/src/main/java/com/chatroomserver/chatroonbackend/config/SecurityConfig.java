package com.chatroomserver.chatroonbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import java.time.Duration;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 关键修复点：暴露AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/login.html").permitAll()
                        .anyRequest().permitAll()
                )

                // 新增headers配置
                .headers(headers -> headers
                        .frameOptions(frame -> frame
                                .sameOrigin() // 允许同源iframe嵌入
                        )
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("frame-ancestors 'self'") // 允许同源框架嵌套
                        )
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    // 在SecurityConfig中添加测试用户（生产环境应使用数据库）
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("testUser")
                .password(passwordEncoder().encode("testUser"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // 保持其他@Bean定义不变
   //@Bean
   //public RateLimitFilter rateLimitFilter() {
   //    return new RateLimitFilter(10, 10, Duration.ofMinutes(1));
   //}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

