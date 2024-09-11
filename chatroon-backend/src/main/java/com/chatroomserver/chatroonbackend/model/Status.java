package com.chatroomserver.chatroonbackend.model;

/**
 * 定义了通信双方在交流过程中的状态
 */
public enum Status {
    // 客户端加入聊天的状态
    JOIN,
    // 客户端发送消息的状态
    MESSAGE,
    // 客户端离开聊天的状态
    LEAVE
}
