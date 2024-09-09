package com.chatroomserver.chatroonbackend.controller;


import com.chatroomserver.chatroonbackend.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ChatRoomWebSocketTest测试类
 * @date 2023/7/17
 * @author peter
 */
// 使用 Spring 和 Mockito 扩展类，用于处理 Spring 配置和模拟对象
@ExtendWith({SpringExtension.class, MockitoExtension.class})
// 启动 Spring Boot 应用程序进行测试，使用随机端口运行嵌入式 Web 环境
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatRoomWebSocketTest {

    // 定义WebSocket的主题路径
    static final String WEBSOCKET_TOPIC = "/chatroom/public";

    // 使用阻塞队列来接收WebSocket消息
    BlockingQueue<String> blockingQueue;
    // WebSocket的STOMP客户端
    WebSocketStompClient stompClient;

    // 注入本地服务器端口
    @LocalServerPort
    private int port;

    // 对象映射器，用于将对象转换为JSON
    private ObjectMapper objectMapper;

    /**
     * 在每个测试开始前运行，用于初始化测试环境
     */
    @BeforeEach
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
        objectMapper = new ObjectMapper(); // 初始化ObjectMapper
    }

    /**
     * 测试从服务器接收消息的功能
     * 验证能否从服务器接收一条消息
     */
    @Test
    void shouldReceiveAMessageFromTheServer() throws Exception {
        // 构建连接到WebSocket服务器的URL
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient
                .connect(url, new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);
        session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());

        // 创建并配置消息对象
        Message message = new Message();
        message.setMessage("This is testPrivateMessage");
        message.setSenderName("User2");
        message.setReceiverName("User1");

        // 将Message对象转换为JSON字符串
        String jsonMessage = objectMapper.writeValueAsString(message);

        // 发送消息到指定的主题
        session.send(WEBSOCKET_TOPIC, jsonMessage.getBytes());
        // 验证接收到的消息是否与发送的消息一致
        assertEquals(jsonMessage, blockingQueue.poll(1, SECONDS));
    }

    /**
     * 自定义的StompFrameHandler实现，用于处理接收到的帧
     */
     class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            // 将接收到的字节消息转换为字符串，并放入阻塞队列中
            blockingQueue.offer(new String((byte[]) o));
        }
    }
}
