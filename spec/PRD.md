# Chatroon - 实时聊天应用 PRD

## Why

当前项目 Chatroon 是一个基于 Spring Boot + React 的实时聊天应用，旨在为用户提供基于 WebSocket 的即时通信体验，支持公共聊天室广播、点对点私聊以及多媒体文件传输。本文档通过对工程代码的全面逆向分析，完整还原产品的功能需求、业务规则、技术约束和验收标准，确保 PRD 与工程实际实现及原始设计意图完全一致。

## What Changes

本文档为新建 PRD 文档，不涉及现有代码的修改。目的是将现有工程的全部功能需求、业务规则、交互逻辑、技术约束、验收标准整理形成完整的规范文档。

## Impact

- 影响范围：文档产出，不影响现有代码
- 关键文件系统：前端 `chatroom-ui/`、后端 `chatroon-backend/`

---

# 1. 产品概述

## 1.1 产品名称
Chatroon（聊天室）

## 1.2 产品定位
轻量级 Web 实时聊天应用，支持公共聊天室、私聊和多媒体传输，面向小规模用户群体的即时通信场景。

## 1.3 技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| 编程语言 | Java | 17 |
| Web 服务器 | Undertow（嵌入式） | 随 Spring Boot |
| 实时通信 | WebSocket + STOMP + SockJS | - |
| 限流组件 | Bucket4j | 7.5.0 |
| 前端框架 | React | ^18.2.0 |
| 前端构建 | Vite | - |
| 前端路由 | React Router | ^5.3.4 |
| 前端 WebSocket 客户端 | SockJS Client + StompJS | ^1.6.1 / ^2.3.3 |
| UI 样式 | Bootstrap | ^5.3.0 |
| 容器化 | Docker + Nginx | - |
| CI/CD | GitHub Actions | - |
| 开源协议 | MIT License | - |

## 1.4 系统架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                      用户浏览器                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  React 前端 (Vite, 端口 5173 开发 / 23110 生产)        │  │
│  │  - Login.jsx (登录页)                                   │  │
│  │  - ChatPage2.jsx (聊天页)                               │  │
│  │  - App.jsx (路由管理)                                   │  │
│  └───────────────────────┬───────────────────────────────┘  │
│                          │ SockJS + STOMP (WebSocket)       │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Spring Boot 后端 (Undertow, 端口 8080)                 │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │ WebSocketConfig                                 │  │  │
│  │  │  - STOMP 端点: /ws (SockJS)                      │  │  │
│  │  │  - 应用前缀: /app                                │  │  │
│  │  │  - 代理前缀: /chatroom, /user                    │  │  │
│  │  ├─────────────────────────────────────────────────┤  │  │
│  │  │ ChatController                                  │  │  │
│  │  │  - @MessageMapping("/message") -> 群聊广播       │  │  │
│  │  │  - @MessageMapping("/private-message") -> 私聊   │  │  │
│  │  │  - @GetMapping("/send") -> HTTP 测试端点         │  │  │
│  │  ├─────────────────────────────────────────────────┤  │  │
│  │  │ RateLimitFilter                                 │  │  │
│  │  │  - 基于 IP 的令牌桶限流 (Bucket4j)               │  │  │
│  │  │  - 每 IP 每分钟最多 10 次 HTTP 请求               │  │  │
│  │  ├─────────────────────────────────────────────────┤  │  │
│  │  │ CorsConfig                                      │  │  │
│  │  │  - 允许所有来源/方法/头 (开发环境配置)             │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 1.5 项目目录结构

```
Springboot-chatapp/
├── .github/workflows/          # CI/CD 流水线
│   ├── ci.yml                  # 构建+测试流水线
│   └── deploy.yml              # 部署流水线（蓝绿部署）
├── chatroom-ui/                # React 前端项目
│   ├── docker/                 # 前端 Docker + Nginx 配置
│   ├── src/
│   │   ├── Layout/
│   │   │   ├── Login.jsx       # 登录页面
│   │   │   ├── ChatPage.jsx    # 聊天页面 v1
│   │   │   ├── ChatPage2.jsx   # 聊天页面 v2（实际使用）
│   │   │   └── *.test.jsx      # 前端测试文件
│   │   ├── App.jsx             # 路由配置
│   │   └── main.jsx            # React 入口
│   ├── package.json
│   └── vite.config.js
├── chatroon-backend/           # Spring Boot 后端项目
│   ├── src/main/java/com/chatroomserver/chatroonbackend/
│   │   ├── ChatroonBackendApplication.java   # 启动类
│   │   ├── config/                           # 配置类
│   │   │   ├── WebSocketConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── RateLimitFilter.java
│   │   ├── controller/
│   │   │   └── ChatController.java           # 聊天控制器
│   │   └── model/
│   │       ├── Message.java                  # 消息模型
│   │       └── Status.java                   # 状态枚举
│   ├── src/main/resources/
│   │   ├── application.properties            # 配置文件（空）
│   │   └── static/                           # 内嵌 Demo 页面
│   ├── src/test/                             # 后端单元测试
│   ├── pom.xml
│   └── Dockerfile
├── README.md
└── LICENSE
```

---

# 2. 功能需求

## 2.1 用户认证（用户登录）

### 需求描述
系统 SHALL 提供基于用户名的轻量级身份标识功能，用户进入应用前须输入用户名作为身份标识。

### 业务规则
- BR-2.1.1: 用户名必填，不可为空
- BR-2.1.2: 用户名通过浏览器 localStorage 持久化存储（key: `chat-username`）
- BR-2.1.3: 已登录用户（localStorage 中存在 `chat-username`）访问 `/` 时自动重定向到 `/chat`
- BR-2.1.4: 未登录用户访问 `/chat` 时自动重定向到 `/login`
- BR-2.1.5: 当前实现不包含密码验证、用户注册或服务端会话管理

### 交互逻辑
1. 用户访问应用，展示登录页面
2. 用户在输入框中输入用户名
3. 按回车键或点击 "Connect" 按钮触发登录
4. 用户名存入 localStorage，页面跳转到 `/chat`
5. 登录页背景使用随机图片（picsum.photos）

### 验收标准
- AC-2.1.1: 输入用户名后按回车键可成功登录并跳转到聊天页
- AC-2.1.2: 输入用户名后点击 Connect 按钮可成功登录
- AC-2.1.3: 用户名为空时不允许登录
- AC-2.1.4: 已登录用户直接访问根路径自动跳转到聊天页
- AC-2.1.5: 刷新页面后用户登录状态保持

---

## 2.2 WebSocket 实时通信

### 需求描述
系统 SHALL 通过 WebSocket 协议（STOMP over SockJS）实现客户端与服务端之间的全双工实时消息通信。

### 业务规则
- BR-2.2.1: STOMP 端点为 `/ws`，使用 SockJS 作为 WebSocket 的降级方案
- BR-2.2.2: 客户端发送消息的目标前缀为 `/app`
- BR-2.2.3: 消息代理前缀为 `/chatroom`（公共频道）和 `/user`（私信频道）
- BR-2.2.4: WebSocket 传输超时设置为 60 秒
- BR-2.2.5: 发送缓冲区大小限制为 50MB
- BR-2.2.6: 单条消息大小限制为 50MB
- BR-2.2.7: 连接建立后客户端自动订阅公共频道和私信频道

### 交互逻辑
1. 用户进入聊天页后自动建立 WebSocket 连接
2. 连接成功后订阅 `/chatroom/public` 公共频道
3. 连接成功后订阅 `/user/{username}/private` 私信频道
4. 连接断开时触发错误处理逻辑

### 验收标准
- AC-2.2.1: 用户进入聊天页后 WebSocket 连接成功建立
- AC-2.2.2: 不支持 WebSocket 的浏览器通过 SockJS 自动降级
- AC-2.2.3: 连接断开后用户能感知连接状态变化

---

## 2.3 公共聊天室（群聊）

### 需求描述
系统 SHALL 提供公共聊天室功能，所有在线用户可在同一频道内进行实时群聊。

### 业务规则
- BR-2.3.1: 公共消息发送到 STOMP 目标 `/app/message`
- BR-2.3.2: 服务端接收后广播到 `/chatroom/public` 频道
- BR-2.3.3: 所有已订阅该频道的客户端均收到广播消息
- BR-2.3.4: 消息内容使用 `HtmlUtils.htmlEscape()` 进行 XSS 防护
- BR-2.3.5: 消息包含发送者名称（senderName）、消息内容（message）、状态（status）

### 交互逻辑
1. 用户在聊天室 Tab 的输入框中输入消息
2. 点击 "Send" 按钮或按回车键发送消息
3. 消息以 JSON 格式发送到 `/app/message`
4. 服务端接收后广播到所有订阅者
5. 当前用户的消息以蓝色气泡靠右显示
6. 其他用户的消息以白色气泡靠左显示

### 验收标准
- AC-2.3.1: 用户发送消息后，所有在线用户能即时收到
- AC-2.3.2: 消息气泡区分当前用户和他人（蓝色靠右 vs 白色靠左）
- AC-2.3.3: 消息内容中的 HTML 标签被正确转义，防止 XSS 攻击
- AC-2.3.4: 空消息不允许发送

---

## 2.4 点对点私聊

### 需求描述
系统 SHALL 提供点对点私聊功能，用户可以向特定用户发送私密消息，仅双方可见。

### 业务规则
- BR-2.4.1: 私聊消息发送到 STOMP 目标 `/app/private-message`
- BR-2.4.2: 服务端通过 `SimpMessagingTemplate.convertAndSendToUser()` 将消息发送到 `/user/{receiverName}/private`
- BR-2.4.3: 私聊消息仅发送者和接收者可见
- BR-2.4.4: 接收者名称进行 XSS 转义处理
- BR-2.4.5: 私聊消息包含 senderName、receiverName、message、status 字段

### 交互逻辑
1. 聊天页顶部显示 "Chat Room" 和 "Private" 两个 Tab
2. 用户点击 Private Tab 查看私聊列表
3. 点击特定用户进入私聊会话
4. 在私聊输入框中输入消息并发送
5. 消息仅在双方的私聊窗口中显示

### 验收标准
- AC-2.4.1: 用户可切换公共聊天室和私聊 Tab
- AC-2.4.2: 私聊消息仅发送者和接收者可见
- AC-2.4.3: 私聊消息正确显示发送者名称

---

## 2.5 用户上下线通知

### 需求描述
系统 SHALL 在用户加入或离开聊天室时通知所有在线用户。

### 业务规则
- BR-2.5.1: 用户加入时发送 Status.JOIN 类型消息
- BR-2.5.2: 用户离开时发送 Status.LEAVE 类型消息
- BR-2.5.3: 上下线通知广播到 `/chatroom/public` 频道
- BR-2.5.4: 通知消息包含用户名称和状态类型

### 交互逻辑
1. 用户成功登录并连接 WebSocket 后，自动发送 JOIN 消息
2. 其他用户收到 JOIN 通知，在聊天区域显示"XXX 加入聊天室"
3. 用户点击 Logout 或关闭页面时发送 LEAVE 消息
4. 其他用户收到 LEAVE 通知，在聊天区域显示"XXX 离开聊天室"

### 验收标准
- AC-2.5.1: 新用户加入时其他用户能看到加入通知
- AC-2.5.2: 用户离开时其他用户能看到离开通知
- AC-2.5.3: 上下线通知正确显示用户名

---

## 2.6 多媒体传输

### 需求描述
系统 SHALL 支持用户发送图片和视频等多媒体文件。

### 业务规则
- BR-2.6.1: 多媒体文件通过 FileReader API 转为 Base64 编码传输
- BR-2.6.2: 消息模型中的 `media` 字段存储 Base64 编码的文件数据
- BR-2.6.3: 消息模型中的 `mediaType` 字段标识媒体类型
- BR-2.6.4: 支持图片和视频类型的媒体文件
- BR-2.6.5: 图片消息直接在聊天区域展示缩略图/预览
- BR-2.6.6: 视频消息在聊天区域展示视频播放器预览

### 交互逻辑
1. 用户在聊天页面点击文件选择按钮
2. 选择本地图片或视频文件
3. 前端将文件读取为 Base64 编码
4. 构造包含 media 和 mediaType 的消息对象
5. 通过 WebSocket 发送（公共频道或私聊）
6. 接收方在聊天区域展示媒体预览

### 验收标准
- AC-2.6.1: 用户可选择本地图片文件并发送
- AC-2.6.2: 用户可选择本地视频文件并发送
- AC-2.6.3: 图片消息在聊天区域正确显示预览
- AC-2.6.4: 视频消息在聊天区域可播放预览
- AC-2.6.5: 媒体文件通过公共频道和私聊均可发送

---

## 2.7 用户登出

### 需求描述
系统 SHALL 提供用户登出功能，清除本地登录状态并通知其他用户。

### 业务规则
- BR-2.7.1: 登出时清除 localStorage 中的 `chat-username`
- BR-2.7.2: 登出前发送 Status.LEAVE 消息通知其他用户
- BR-2.7.3: 登出后断开 WebSocket 连接
- BR-2.7.4: 登出后页面跳转回登录页

### 交互逻辑
1. 用户点击聊天页面右上角的 "Logout" 按钮
2. 前端发送 LEAVE 状态消息到公共频道
3. 清除 localStorage 中的登录信息
4. 断开 STOMP 客户端连接
5. 路由跳转到 `/login` 页面

### 验收标准
- AC-2.7.1: 点击 Logout 后用户被跳转到登录页
- AC-2.7.2: 登出后 localStorage 中无 `chat-username`
- AC-2.7.3: 登出后其他用户收到离开通知

---

## 2.8 HTTP 请求限流

### 需求描述
系统 SHALL 对 HTTP 请求实施基于 IP 的速率限制，防止恶意请求。

### 业务规则
- BR-2.8.1: 使用令牌桶算法（Token Bucket）实现限流
- BR-2.8.2: 每个 IP 地址独立维护一个令牌桶
- BR-2.8.3: 桶容量为 10，每分钟补充 10 个令牌
- BR-2.8.4: 超出限制的请求返回 HTTP 429 (Too Many Requests)
- BR-2.8.5: 限流基于客户端 IP 地址（`request.getRemoteAddr()`）
- BR-2.8.6: 使用 ConcurrentHashMap 为每个 IP 维护独立的 Bucket 实例

### 交互逻辑
1. 每个 HTTP 请求经过 RateLimitFilter
2. 过滤器根据请求 IP 查找或创建对应的令牌桶
3. 尝试消费一个令牌
4. 成功则放行请求，失败则返回 429 错误

### 验收标准
- AC-2.8.1: 正常频率请求（每分钟少于 10 次）正常处理
- AC-2.8.2: 超频请求（每分钟超过 10 次）返回 HTTP 429
- AC-2.8.3: 不同 IP 的限流互不影响

---

## 2.9 CORS 跨域支持

### 需求描述
系统 SHALL 配置 CORS 策略以支持前后端分离部署。

### 业务规则
- BR-2.9.1: 允许所有来源域名访问（`*`）
- BR-2.9.2: 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
- BR-2.9.3: 允许所有请求头
- BR-2.9.4: CORS 配置适用于所有路径（`*/**`）

### 验收标准
- AC-2.9.1: 前端（端口 5173）可正常请求后端（端口 8080）
- AC-2.9.2: 跨域请求不被浏览器拦截

---

# 3. 数据模型

## 3.1 Message（消息）

| 字段 | 类型 | 说明 | 必填 |
|------|------|------|------|
| senderName | String | 发送者用户名 | 是 |
| receiverName | String | 接收者用户名（私聊时使用） | 否 |
| message | String | 消息文本内容 | 否 |
| media | String | 多媒体文件的 Base64 编码数据 | 否 |
| status | Status | 消息状态枚举 | 是 |
| mediaType | String | 媒体文件类型标识 | 否 |

## 3.2 Status（状态枚举）

| 枚举值 | 说明 | 使用场景 |
|--------|------|----------|
| JOIN | 用户加入聊天室 | 用户登录连接后发送 |
| MESSAGE | 普通消息 | 用户发送文本或媒体消息时 |
| LEAVE | 用户离开聊天室 | 用户登出或断开连接时 |

## 3.3 消息数据流

```
┌──────────┐     STOMP /app/message      ┌──────────────────┐
│  客户端   │ ───────────────────────────> │ ChatController    │
│ (React)  │                              │ receiveMessage()  │
│          │ <─────────────────────────── │ @SendTo           │
│          │   STOMP /chatroom/public     │ /chatroom/public  │
│          │                              └──────────────────┘
│          │
│          │     STOMP /app/private-message  ┌──────────────────┐
│          │ ─────────────────────────────> │ ChatController    │
│          │                                │ privateMessage()  │
│          │ <──────────────────────────── │ convertAndSend    │
│          │   STOMP /user/{name}/private   │ ToUser()          │
└──────────┘                                └──────────────────┘
```

---

# 4. 接口规范

## 4.1 WebSocket STOMP 接入点

### 端点
- **路径**: `/ws`
- **协议**: SockJS (WebSocket 降级)
- **允许来源**: 所有域名（`*`）

### 消息发送端点（客户端 -> 服务端）

| 端点 | 用途 | 消息格式 |
|------|------|----------|
| `/app/message` | 发送公共消息 | Message JSON |
| `/app/private-message` | 发送私聊消息 | Message JSON |

### 消息订阅端点（服务端 -> 客户端）

| 端点 | 用途 |
|------|------|
| `/chatroom/public` | 订阅公共聊天室广播 |
| `/user/{username}/private` | 订阅私聊消息 |

### 消息 JSON 格式示例

```json
{
  "senderName": "Alice",
  "receiverName": "Bob",
  "message": "Hello!",
  "media": null,
  "status": "MESSAGE",
  "mediaType": null
}
```

## 4.2 HTTP 接口

| 方法 | 路径 | 用途 | 响应 |
|------|------|------|------|
| GET | `/send?message={msg}` | 测试回显端点 | 返回 message 参数值 |

---

# 5. 技术约束

## 5.1 运行环境
- JDK 17
- Node.js 20（前端构建）
- Maven 3.x（后端构建）

## 5.2 端口配置

| 服务 | 开发环境端口 | 生产环境端口 |
|------|-------------|-------------|
| React 前端 | 5173（Vite DevServer） | 23110（Nginx） |
| Spring Boot 后端 | 8080 | 23111 |

## 5.3 传输限制
- WebSocket 发送超时：60 秒
- WebSocket 发送缓冲区：50MB
- WebSocket 消息大小限制：50MB
- HTTP 限流：每 IP 每分钟 10 次

## 5.4 数据持久化
- **当前无数据库**：所有消息仅为实时转发，不做持久化存储
- 用户身份仅通过浏览器 localStorage 维护
- 服务端无用户会话或消息历史记录

## 5.5 安全约束
- XSS 防护：消息内容通过 `HtmlUtils.htmlEscape()` 转义
- 速率限制：基于 Bucket4j 的 IP 级令牌桶限流
- CORS：当前为完全开放策略（适合开发环境）
- 无用户认证/授权体系（无密码、无 JWT、无 Spring Security）

---

# 6. 部署架构

## 6.1 容器化方案

### 后端 Docker 镜像
- 基础镜像：`eclipse-temurin:17-jdk-alpine`
- 时区配置：`Asia/Shanghai`
- 入口命令：`java -Djava.security.egd=file:/dev/./urandom -jar /app.jar`

### 前端 Docker 镜像
- 基础镜像：网易 Nginx 镜像
- 监听端口：23110
- 反向代理：`/back/` 路径代理到后端 `172.21.44.1:23111`
- WebSocket 代理：支持 `Upgrade` 和 `Connection` 头透传

## 6.2 CI/CD 流水线

### CI 流水线（ci.yml）
触发条件：push 或 PR 到 master/main 分支

| Job | 步骤 |
|-----|------|
| backend-build | Checkout -> Setup Java 17 -> `mvn test` -> `mvn clean package` -> 上传 JAR |
| frontend-build | Checkout -> Setup Node 20 -> `npm install` -> `npm run lint` -> `vitest run` -> `npm run build` -> 上传 dist |

### 部署流水线（deploy.yml）
四阶段流水线：security-scan -> test -> build -> deploy（蓝绿部署）

---

# 7. 测试策略

## 7.1 后端测试

| 测试文件 | 测试类型 | 测试内容 |
|----------|----------|----------|
| ChatControllerTest.java | 集成测试 | WebSocket 公共消息发送、私聊消息发送 |
| ChatRoomWebSocketTest.java | 集成测试 | WebSocket 消息收发验证 |
| MessageTest.java | 单元测试 | Message 模型属性验证 |
| ChatroonBackendApplicationTests.java | 启动测试 | Spring 上下文加载验证 |
| RateLimitFilterMarsCodeTest.java | 单元测试 | 限流过滤器逻辑验证 |
| ChatControllerDiffblueTest.java | 单元测试 | Diffblue 自动生成的控制器测试 |

### 后端测试工具
- JUnit 5 + SpringBootTest
- Mockito（模拟 SimpMessagingTemplate）
- Awaitility（异步测试断言）

## 7.2 前端测试

| 测试文件 | 测试内容 |
|----------|----------|
| Login.test.jsx | 登录页渲染、用户输入、回车登录 |
| ChatPage.test.jsx | 聊天页渲染、消息发送、文件上传 |
| ChatPage2.test.jsx | ChatPage2 渲染、消息输入发送、私聊、文件上传、登出、键盘事件 |

### 前端测试工具
- Vitest + jsdom
- React Testing Library
- Mock（SockJS Client、StompJS）

---

# 8. 已知限制与技术债务

## 8.1 功能限制
- 无用户注册/密码认证机制，仅凭用户名标识身份
- 无消息持久化，页面刷新后聊天记录丢失
- 无群组/房间管理功能，仅支持单一公共聊天室
- 多媒体文件以 Base64 传输，大文件会导致消息体积膨胀
- 无消息已读/未读状态追踪

## 8.2 安全风险
- CORS 策略完全开放，生产环境需收紧
- 无 Spring Security 认证授权框架
- RateLimitFilter 的 ConcurrentHashMap 中 IP 对应的 Bucket 永不清理，大量不同 IP 访问可能导致内存增长
- 用户名无唯一性校验，可能存在身份冒用

## 8.3 架构改进点
- 无 Service 层抽象，业务逻辑直接写在 Controller 中
- `application.properties` 为空，无外部化配置
- 前端 WebSocket 地址硬编码为 `http://localhost:8080/ws`
- 前端路由实现中 `useHistory` 在渲染期间调用 `history.push()`，存在潜在的渲染循环风险
