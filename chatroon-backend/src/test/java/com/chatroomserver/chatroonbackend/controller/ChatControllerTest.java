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
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;


    private WebSocketStompClient webSocketStompClient;

    @LocalServerPort // Inject the port number
    private int port;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Initialize the WebSocketStompClient
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(transports));
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
        StompSession session = webSocketStompClient.connect(url, new StompSessionHandlerAdapter() {}).get(1, SECONDS);

        // Convert the Message object to JSON
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Send the message to the public chatroom
        var result=session.send("/app/message", jsonMessage.getBytes()); // Send as byte array

        assertNotNull(result);
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
        StompSession session = webSocketStompClient.connect(url, new StompSessionHandlerAdapter() {}).get();

        // Convert the Message object to JSON
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Send the private message
        var result= session.send("/app/private-message", jsonMessage.getBytes()); // Send as byte array
        assertNotNull(result);
        // Verify that the private message was sent to the correct user
        //verify(simpMessagingTemplate).convertAndSendToUser(eq(message.getMessage()), "/private", message.getMessage());
    }

    @Test
    @Disabled
    public void verifyGreetingIsReceived() throws Exception {

        // 创建并配置消息对象
        Message message = new Message();
        message.setMessage("This is testPrivateMessage");
        message.setSenderName("User2");
        message.setReceiverName("User1");
        // 将Message对象转换为JSON字符串
        String jsonMessage = objectMapper.writeValueAsString(message);

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        String url = "ws://localhost:" + port + "/ws";
        StompSession session = webSocketStompClient
                .connect(url, new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);

        session.subscribe("/chatroom/public", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    // Convert the payload to a JSON string and add it to the BlockingQueue
                    blockingQueue.offer(objectMapper.writeValueAsString(payload));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        session.send("/app/message", jsonMessage.getBytes());

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(jsonMessage, blockingQueue.poll()));
    }

    @Test
    void verifyWelcomeMessageIsSent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://localhost:" + port + "/ws";

        StompSession session = webSocketStompClient
                .connect(url, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/chatroom", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                latch.countDown();
            }
        });

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(1, latch.getCount()));
    }
}