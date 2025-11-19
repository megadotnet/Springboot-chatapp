package com.chatroomserver.chatroonbackend.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;


import com.chatroomserver.chatroonbackend.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.awaitility.Awaitility.await;

/**
 * Unit tests for the ChatController class.
 */
@ExtendWith({MockitoExtension.class})
public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ChatController chatController;
    @Test
    public void testReceiveMessage_withXssAttempt() throws Exception {
        Message message = new Message();
        message.setMessage("<script>alert('XSS')</script>");
        message.setSenderName("User2");
        message.setReceiverName("User1");

        Message result = chatController.receiveMessage(message);

        assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;", result.getMessage());
    }

    @Test
    public void testPrivateMessage_withXssAttempt() throws Exception {
        Message message = new Message();
        message.setMessage("<script>alert('XSS')</script>");
        message.setSenderName("User2");
        message.setReceiverName("User1");

        Message result = chatController.privateMessage(message);

        assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;", result.getMessage());
    }
}