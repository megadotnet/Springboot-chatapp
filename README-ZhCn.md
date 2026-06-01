# Chat Application Project

[English](README.md) | 简体中文

## 📖 项目介绍
这是一个支持实时通讯的聊天室应用程序，基于全栈分离架构设计。后端采用 Java Spring Boot 构建稳定可靠的服务，前端使用 React 与 Vite 构建现代化交互界面。客户端与服务端之间通过 SockJS 和 STOMP 协议实现全双工的 WebSocket 实时通信。该项目旨在提供一个开箱即用的轻量级即时聊天解决方案。

核心功能包括：
- **用户认证**：简单的用户名登录即可加入聊天。
- **实时群聊**：登录后自动加入公共聊天室，实时收发消息。
- **上下线通知**：广播新用户加入或离开聊天室的动态。
- **私信功能**：支持选择特定在线用户发送私密消息。
- **多媒体传输**：支持图片、视频等多媒体文件的分享。

## 🛠 技术栈清单

### 前端技术栈
- **React (`^18.2.0`)**: 核心视图层库，负责构建基于组件的用户界面。
- **React-Router-DOM (`^5.3.4`)**: 处理前端路由，管理页面跳转与状态。
- **Vite (`^4.3.9`)**: 下一代前端构建工具，提供极速的冷启动和热重载。
- **SockJS-Client (`^1.6.1`)**: 提供类似 WebSocket 的对象，在不支持 WebSocket 的环境中提供降级方案。
- **STOMP.js (`^2.3.3`)**: 实现了 STOMP 协议的客户端，用于在 WebSocket 连接上进行消息发布与订阅。
- **Bootstrap (`^5.3.0`)**: 提供响应式的 UI 组件，加速页面样式的开发。

### 后端技术栈
- **Java (`17`)**: 后端核心编程语言。
- **Spring Boot (`3.2.0`)**: 快速构建独立的生产级应用。
- **Spring WebSocket**: 提供核心的 WebSocket 通讯能力与 STOMP 协议支持。
- **Undertow**: 替代 Tomcat 的轻量级、高性能的 Web 服务器。
- **Bucket4j (`7.5.0`)**: 基于令牌桶算法的限流组件，用于保护 API 防御 CC 攻击。
- **Lombok**: 减少实体类中 getter/setter/构造器等样板代码。

### 基础设施
- **无数据库**: 当前架构仅在内存中管理用户与消息，适合轻量级部署。（注：未来可轻易扩展集成 MySQL/Redis 等数据源）。

### 工具链
- **Maven (`3.x`)**: Java 项目的依赖管理与构建工具。
- **npm / Node.js**: 前端依赖包管理与运行环境。
- **Vitest (`^4.1.7`)**: 前端的高速单元测试框架。
- **JUnit 5 / Mockito**: 后端自动化测试框架。
- **Jacoco (`0.8.11`)**: 后端代码测试覆盖率统计工具。
- **GitHub Actions**: 提供自动化的 CI/CD 流程，处理自动化的构建与测试。

## ⚙️ 环境依赖要求
为了避免环境冲突，请确保您的开发环境满足以下**最低**兼容要求：
- **Java JDK**: `17` 或更高版本。
- **Maven**: `3.6.0` 或更高版本。
- **Node.js**: `20.x` 或更高版本。
- **npm**: `10.x` 或更高版本。
- **Git**: `2.x`。

## 🚀 本地部署与启动步骤

以下操作指令适用于主流的 Windows, macOS, Linux 环境：

### 1. 克隆项目
```bash
git clone https://github.com/Kshitijk5/Springboot-chatapp.git
cd Springboot-chatapp
```

### 2. 后端部署 (Spring Boot)
```bash
# 进入后端工程目录
cd chatroom-backend

# 下载依赖并编译打包 (忽略测试)
mvn clean package -DskipTests

# 启动 Spring Boot 服务 (默认端口通常为 8080)
mvn spring-boot:run
```
*(注：Windows 用户在 PowerShell 中如果遇到 `-DskipTests` 解析错误，请使用 `mvn clean package "-DskipTests"`)*

### 3. 前端部署 (React)
请打开一个**新的终端窗口**并执行：
```bash
# 返回根目录并进入前端工程目录
cd chatroom-ui

# 安装前端依赖
npm install

# 启动 Vite 开发服务器
npm run dev
```

### 4. 访问应用
在浏览器中打开: `http://localhost:5173` 进行访问和测试。

## 📂 项目结构说明
```text
.
├── chatroom-ui/             # 前端 React 项目目录
│   ├── src/                 # 源代码目录 (组件、样式、服务等)
│   ├── package.json         # 前端依赖与脚本配置
│   └── vite.config.js       # Vite 构建配置
│
├── chatroom-backend/        # 后端 Spring Boot 项目目录
│   ├── src/main/java/       # 后端 Java 源代码 (控制器、配置类、业务逻辑等)
│   ├── src/main/resources/  # 配置文件 (application.properties 等)
│   ├── src/test/            # 单元测试代码
│   └── pom.xml              # Maven 依赖与构建配置
│
├── .github/workflows/       # GitHub Actions CI/CD 配置文件
├── README.md                # 英文说明文档
└── README-ZhCn.md           # 中文说明文档
```

## 📝 开发规范
- **代码提交**: 请遵循通用的 Commit Message 规范 (如 `feat:`, `fix:`, `docs:`)，提交信息应清晰简练。
- **后端测试**: 编写新的功能或修复 Bug 后，建议补充对应的 JUnit 单元测试，保证核心逻辑不被破坏。
- **前端测试**: 组件或公共函数的修改需尽可能使用 `Vitest` 补充测试用例，并确保 `npm run lint` 检查无警告。
- **代码格式化**:
  - 前端：遵守 ESLint 检查规范。
  - 后端：遵守标准的 Java 编码规范，推荐使用 Lombok 的注解来减少冗余样板代码。

## ❓ 常见问题排查

**Q1: 前端 `npm install` 时速度极慢或经常超时怎么办？**
- A: 建议切换使用淘宝等国内镜像源，执行: `npm config set registry https://registry.npmmirror.com` 后重新重试安装。

**Q2: 启动后端时提示 "Port 8080 already in use" 端口被占用？**
- A: 端口被其他程序占用。
  - Windows 下可通过 `netstat -ano | findstr 8080` 找出 PID，然后用 `taskkill /F /PID <PID>` 结束进程。
  - macOS/Linux 可使用 `kill $(lsof -t -i:8080)` 命令强行关闭占用端口的进程。

**Q3: 前后端无法成功建立 WebSocket 连接？**
- A: 请检查后端的 CORS (跨域资源共享) 配置是否允许了前端的域名和端口 (`http://localhost:5173`) 访问，并确保前端中 SockJS 实例的连接地址与后端启动地址精确匹配。

**Q4: Maven 下载依赖失败或过慢？**
- A: 建议检查并修改本地的 Maven `settings.xml` 配置，添加国内的 Maven 镜像代理 (如阿里云镜像)。
