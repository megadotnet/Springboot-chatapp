# Chat Application Project

English | [简体中文](README-ZhCn.md)

## 📖 Project Introduction
This is a real-time chat application project designed with a separated full-stack architecture. The backend uses Java Spring Boot to provide stable and reliable services, while the frontend utilizes React and Vite to build a modern interactive interface. Full-duplex real-time communication between the client and server is achieved using SockJS and the STOMP protocol. This project aims to provide an out-of-the-box, lightweight instant messaging solution.

Core features include:
- **User Authentication**: Simply enter a username to join the chat.
- **Real-time Group Chat**: Automatically join the public chatroom upon login to send and receive messages in real-time.
- **Presence Notifications**: Broadcast notifications when new users join or leave the chatroom.
- **Private Messaging**: Support for sending private messages to specific online users.
- **Multimedia Transfer**: Support for sharing multimedia files such as photos and videos.

## 🛠 Tech Stack

### Frontend
- **React (`^18.2.0`)**: The core view library responsible for building a component-based user interface.
- **React-Router-DOM (`^5.3.4`)**: Handles frontend routing, managing page transitions and state.
- **Vite (`^4.3.9`)**: A next-generation frontend build tool providing extremely fast cold starts and Hot Module Replacement (HMR).
- **SockJS-Client (`^1.6.1`)**: Provides a WebSocket-like object, offering a fallback in environments where WebSockets are not supported.
- **STOMP.js (`^2.3.3`)**: Implements the STOMP protocol client for publishing and subscribing to messages over a WebSocket connection.
- **Bootstrap (`^5.3.0`)**: Provides responsive UI components to accelerate page styling.

### Backend
- **Java (`17`)**: The core programming language for the backend.
- **Spring Boot (`3.2.0`)**: Used to quickly build standalone, production-grade applications.
- **Spring WebSocket**: Provides core WebSocket communication capabilities and STOMP protocol support.
- **Undertow**: A lightweight, high-performance web server that acts as an alternative to Tomcat.
- **Bucket4j (`7.5.0`)**: A rate-limiting component based on the token bucket algorithm, used to protect the API against CC attacks.
- **Lombok**: Reduces boilerplate code such as getters, setters, and constructors in entity classes.

### Infrastructure
- **No Database**: The current architecture manages users and messages strictly in-memory, making it suitable for lightweight deployment. (Note: It can be easily extended to integrate data sources like MySQL/Redis in the future).

### Toolchain
- **Maven (`3.x`)**: Dependency management and build tool for Java projects.
- **npm / Node.js**: Frontend dependency package management and runtime environment.
- **Vitest (`^4.1.7`)**: A fast frontend unit testing framework.
- **JUnit 5 / Mockito**: Backend automated testing frameworks.
- **Jacoco (`0.8.11`)**: Backend code test coverage statistics tool.
- **GitHub Actions**: Provides automated CI/CD pipelines to handle automated builds and tests.

## ⚙️ Environment Dependencies
To avoid environment conflicts, please ensure your development environment meets the following **minimum** compatibility requirements:
- **Java JDK**: `17` or higher.
- **Maven**: `3.6.0` or higher.
- **Node.js**: `20.x` or higher.
- **npm**: `10.x` or higher.
- **Git**: `2.x`.

## 🚀 Local Deployment and Setup Steps

The following command instructions are applicable to mainstream Windows, macOS, and Linux environments:

### 1. Clone the Project
```bash
git clone https://github.com/Kshitijk5/Springboot-chatapp.git
cd Springboot-chatapp
```

### 2. Backend Deployment (Spring Boot)
```bash
# Navigate to the backend project directory
cd chatroom-backend

# Download dependencies and build the package (skipping tests)
mvn clean package -DskipTests

# Start the Spring Boot server (default port is usually 8080)
mvn spring-boot:run
```
*(Note: For Windows users using PowerShell, if you encounter a parsing error with `-DskipTests`, please use `mvn clean package "-DskipTests"`)*

### 3. Frontend Deployment (React)
Please open a **new terminal window** and execute:
```bash
# Return to the root directory and navigate to the frontend project directory
cd chatroom-ui

# Install frontend dependencies
npm install

# Start the Vite development server
npm run dev
```

### 4. Access the Application
Open your browser and navigate to: `http://localhost:5173` to access and test the application.

## 📂 Project Structure Overview
```text
.
├── chatroom-ui/             # Frontend React project directory
│   ├── src/                 # Source code directory (components, styles, services, etc.)
│   ├── package.json         # Frontend dependencies and script configurations
│   └── vite.config.js       # Vite build configuration
│
├── chatroom-backend/        # Backend Spring Boot project directory
│   ├── src/main/java/       # Backend Java source code (controllers, config classes, business logic, etc.)
│   ├── src/main/resources/  # Configuration files (application.properties, etc.)
│   ├── src/test/            # Unit test code
│   └── pom.xml              # Maven dependencies and build configuration
│
├── .github/workflows/       # GitHub Actions CI/CD configuration files
├── README.md                # English documentation
└── README-ZhCn.md           # Chinese documentation
```

## 📝 Development Guidelines
- **Code Commits**: Please follow common Commit Message conventions (e.g., `feat:`, `fix:`, `docs:`), and keep commit messages clear and concise.
- **Backend Testing**: When writing new features or fixing bugs, it is recommended to add corresponding JUnit unit tests to ensure core logic is not broken.
- **Frontend Testing**: Modifications to components or public functions should be supplemented with test cases using `Vitest` whenever possible, and ensure `npm run lint` completes without warnings.
- **Code Formatting**:
  - Frontend: Adhere to ESLint linting rules.
  - Backend: Follow standard Java coding conventions, and use Lombok annotations to reduce boilerplate code where recommended.

## ❓ Common Troubleshooting

**Q1: What should I do if `npm install` is very slow or frequently times out in the frontend?**
- A: It is recommended to use a mirror registry, or check your internet connection.

**Q2: Starting the backend prompts "Port 8080 already in use"?**
- A: The port is occupied by another program.
  - On Windows, use `netstat -ano | findstr 8080` to find the PID, then terminate it using `taskkill /F /PID <PID>`.
  - On macOS/Linux, use `kill $(lsof -t -i:8080)` to forcefully close the process occupying the port.

**Q3: The frontend and backend cannot successfully establish a WebSocket connection?**
- A: Please check if the backend's CORS (Cross-Origin Resource Sharing) configuration allows access from the frontend's domain and port (`http://localhost:5173`), and ensure the connection address of the SockJS instance in the frontend exactly matches the backend's startup address.

**Q4: Maven fails to download dependencies or is too slow?**
- A: Consider checking and modifying your local Maven `settings.xml` configuration to add a Maven mirror proxy.