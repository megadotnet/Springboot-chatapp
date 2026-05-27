# GitHub Actions CI/CD 工作流配置说明文档

本文档详细介绍了本项目使用的 GitHub Actions 工作流配置文件 `.github/workflows/ci.yml` 的结构、前后端技术栈配置、构建路径以及各步骤的任务流程。该工作流旨在实现代码提交至指定分支后，全自动触发构建和测试流程，确保代码质量和交付产物的稳定性。

## 一、 项目技术栈概述

本项目为前后端分离架构的聊天应用 (Chat Application)：

### 1. 前端 (`chatroom-ui`)
- **核心框架**: React (使用 Vite 构建)
- **依赖管理**: npm (`package.json`, `package-lock.json`)
- **Node.js 版本**: v18 及以上
- **代码规范校验**: ESLint
- **单元测试**: Vitest
- **构建产物路径**: `chatroom-ui/dist/`

### 2. 后端 (`chatroon-backend`)
- **核心框架**: Java Spring Boot
- **构建工具**: Maven (`pom.xml`)
- **Java 版本**: Java 17
- **单元测试框架**: JUnit
- **构建产物路径**: `chatroon-backend/target/*.jar`

## 二、 工作流触发条件

工作流将在以下情况下被自动触发：
- 当有代码 `push` 到 `master` 或 `main` 分支时。
- 当有基于 `master` 或 `main` 分支的 `pull_request` 被创建或更新时。

```yaml
on:
  push:
    branches:
      - master
      - main
  pull_request:
    branches:
      - master
      - main
```

## 三、 工作流任务 (Jobs) 解析

为了保证前后端项目的独立性和并行效率，本工作流包含两个独立的 Job：`backend-build` 和 `frontend-build`。它们将在独立的 Ubuntu Runner 上并行执行。

### 1. 后端构建任务 (`backend-build`)

该任务位于 `chatroon-backend` 目录下执行。

#### 核心步骤：
1. **获取源码 (Checkout Code)**: 检出当前仓库代码。
2. **环境配置 (Setup Java 17)**:
   - 使用 `actions/setup-java` 初始化 Java 17 环境 (采用 `temurin` 发行版)。
   - 启用 `maven` 依赖缓存加速构建。
3. **单元测试 (Run Backend Unit Tests)**:
   - 执行命令 `mvn test` 运行后端所有单元测试。
4. **生产环境打包 (Build Production Artifact)**:
   - 执行命令 `mvn clean package -DskipTests` 跳过测试以加快打包速度，生成最终的 `.jar` 产物。
5. **构建产物校验 (Verify Backend Build Artifact)**:
   - 运行 `ls -la target/*.jar` 确保构建产物 `.jar` 文件已被成功生成。
6. **归档上传构建产物 (Upload Backend Artifact)**:
   - 使用 `actions/upload-artifact` 将构建出的 `.jar` 文件上传至 GitHub 构件库，命名为 `backend-artifact`。产物默认保留 7 天。

### 2. 前端构建任务 (`frontend-build`)

该任务位于 `chatroom-ui` 目录下执行。

#### 核心步骤：
1. **获取源码 (Checkout Code)**: 检出当前仓库代码。
2. **环境配置 (Setup Node.js)**:
   - 使用 `actions/setup-node` 配置 Node.js (v18) 运行环境。
   - 启用基于 `package-lock.json` 的 `npm` 依赖缓存以加快安装速度。
3. **依赖安装 (Install Frontend Dependencies)**:
   - 运行 `npm install` 安装所有前端所需的依赖包。
4. **代码规范校验 (Check Linting)**:
   - 运行 `npm run lint` 验证前端代码是否符合 ESLint 规则要求。
5. **单元测试 (Run Frontend Unit Tests)**:
   - 执行 `npx vitest run --passWithNoTests` 运行前端单元测试流程。
6. **生产环境构建打包 (Build Production Frontend)**:
   - 执行 `npm run build`，利用 Vite 将源码编译打包为可部署的静态文件。
7. **构建产物校验 (Verify Frontend Build Artifact)**:
   - 运行 `ls -la dist/` 确保 Vite 成功构建并将产物放置在 `dist/` 目录中。
8. **归档上传构建产物 (Upload Frontend Artifact)**:
   - 使用 `actions/upload-artifact` 将整个 `dist/` 目录上传至 GitHub 构件库，命名为 `frontend-artifact`。产物默认保留 7 天。

## 四、 后续部署建议

当前的工作流实现了全自动的 CI (持续集成) 流程，验证代码逻辑并生成了制品。
如果需要进一步实现 CD (持续部署)，可以在此基础上添加新的部署 Job，利用 `needs` 关键字依赖于当前的构建 Job，通过 `actions/download-artifact` 将产物下载到部署 Runner 中，最终通过脚本/工具推送到服务器 (如 Docker 容器、云服务平台等)。
