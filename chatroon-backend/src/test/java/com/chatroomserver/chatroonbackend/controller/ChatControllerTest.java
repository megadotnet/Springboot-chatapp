package com.chatroomserver.chatroonbackend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;

import com.chatroomserver.chatroonbackend.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for the ChatController class.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;


    private WebSocketStompClient stompClient;

    @LocalServerPort // Inject the port number
    private int port;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Initialize the WebSocketStompClient
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper
    }
    @Test
    public void testReceiveMessage() throws Exception {
        Message message = new Message();
        message.setMessage("This is test");
        message.setSenderName("User2");
        message.setReceiverName("User1");

        // Connect to the WebSocket server on the dynamically assigned port
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();

        // Convert the Message object to JSON
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Send the message to the public chatroom
        session.send("/app/message", jsonMessage.getBytes()); // Send as byte array


        // Verify that the message was sent to the public chatroom
        //verify(simpMessagingTemplate).convertAndSend("/chatroom/public", message);
    }

    @Test
    public void testPrivateMessage() throws Exception {
        Message message = new Message();
        message.setMessage("This is testPrivateMessage");
        message.setSenderName("User2");
        message.setReceiverName("User1");
        // Connect to the WebSocket server on the dynamically assigned port
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();

        // Convert the Message object to JSON
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Send the private message
        session.send("/app/private-message", jsonMessage.getBytes()); // Send as byte array

        // Verify that the private message was sent to the correct user
        //verify(simpMessagingTemplate).convertAndSendToUser(eq(message.getMessage()), "/private", message.getMessage());
    }
}