package com.chatroomserver.chatroonbackend.model;

import lombok.*;

/**
 * 消息类，用于封装消息的详细信息
 * 该类使用了Lombok注解来简化代码，避免手动编写getter、setter、toString等方法
 * 主要属性包括发送者姓名、接收者姓名、消息内容、媒体文件、消息状态和媒体类型
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {
    // 发送者姓名
    private String senderName;

    // 接收者姓名
    private String receiverName;

    // 消息内容
    private String message;

    // 媒体文件路径
    private String media;

    // 消息状态，使用Status枚举表示
    private Status status;

    // 媒体类型，描述媒体文件的类型
    private String mediaType;
}
