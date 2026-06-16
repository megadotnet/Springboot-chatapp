# 聊天室 API 文档

## 概述

本接口文档详细描述了聊天室应用后端的三个主要接口，包括一个HTTP GET接口和两个WebSocket接口。

---

## 接口详情

### 1. 发送消息 (GET /send)

此接口用于通过HTTP GET请求向服务器发送消息。

- **URL:** `/send`
- **请求方法:** `GET`
- **请求参数:**
  - `message` (string, required): 需要发送的消息内容。
- **成功响应 (200 OK):**
  - **内容:** 返回发送成功的消息字符串。
  - **示例:** `Hello, World!`

### 2. 接收公共消息 (WebSocket /message)

此接口用于通过WebSocket接收公共消息。客户端应使用STOMP协议连接此WebSocket端点。

- **URL:** `/message`
- **协议:** `WebSocket`
- **请求体:** `Message` 对象 (JSON格式)
  - `senderName` (string): 发送者姓名。
  - `receiverName` (string): 接收者姓名。
  - `message` (string): 消息内容。
  - `media` (string, optional): 媒体文件路径。
  - `status` (string): 消息状态 (可选: `JOIN`, `MESSAGE`, `LEAVE`)。
  - `mediaType` (string, optional): 媒体文件类型。
- **成功响应 (200 OK):**
  - **内容:** 返回接收到的 `Message` 对象。

### 3. 接收私有消息 (WebSocket /private-message)

此接口用于通过WebSocket接收私有消息。客户端应使用STOMP协议连接此WebSocket端点。

- **URL:** `/private-message`
- **协议:** `WebSocket`
- **请求体:** `Message` 对象 (JSON格式)
  - `senderName` (string): 发送者姓名。
  - `receiverName` (string): 接收者姓名。
  - `message` (string): 消息内容。
  - `media` (string, optional): 媒体文件路径。
  - `status` (string): 消息状态 (可选: `JOIN`, `MESSAGE`, `LEAVE`)。
  - `mediaType` (string, optional): 媒体文件类型。
- **成功响应 (200 OK):**
  - **内容:** 返回接收到的 `Message` 对象。

---
