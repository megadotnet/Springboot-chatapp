package com.chatroomserver.chatroonbackend.controller;

import com.chatroomserver.chatroonbackend.config.JwtTokenProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user.getUsername());

            return ResponseEntity.ok(new LoginResponse(
                token,
                user.getUsername()
            ));

        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
        }
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class LoginResponse {
        private final String token;
        private final String username;
    }

    @Data
    static class ErrorResponse {
        private final String message;
    }
}
