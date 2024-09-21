package com.chatroomserver.chatroonbackend.controller;

import com.chatroomserver.chatroonbackend.model.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

// 使用Spring的Websocket和MVC功能实现消息控制器
@RestController
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * 接收并处理发往"/message"的STOMP消息
     * 将接收到的消息打印到控制台，并将其重新发送到"/chatroom/public"通道
     * @param message 接收到的消息体
     * @return 返回接收到的消息
     * @throws InterruptedException 如果线程被中断
     */
    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@RequestBody Message message) throws InterruptedException {
        log.info("server side :{}", HtmlUtils.htmlEscape(message.getMessage()));
        return message;
    }

    /**
     * 处理发往"/private-message"的STOMP消息
     * 将接收到的消息发送给指定的接收者
     * @param message 接收到的消息体
     * @return 返回接收到的消息
     */
    @MessageMapping("/private-message")
    public Message privateMessage(@RequestBody Message message){
        simpMessagingTemplate.convertAndSendToUser(HtmlUtils.htmlEscape(message.getReceiverName())
                ,"/private",message);
        return message;
    }

    @GetMapping("/send")
    public ResponseEntity<String> sendMessage(String message){
        return ResponseEntity.ok(message);
    }

}
