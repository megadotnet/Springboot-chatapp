// app.js 完整改进方案
var stompClient = null;

function setConnected(connected) {
    // 原有逻辑保持不变
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    // 从本地存储获取实际登录凭证
    const token = localStorage.getItem("chat-token");
    const username = localStorage.getItem("chat-username");

    if (!token || !username) {
        alert("请先完成用户登录");
        window.location.href = "/login.html"; // 跳转登录页
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // 构建符合后端拦截器要求的请求头
    const headers = {
        "Authorization": `Bearer ${token}`, // 匹配WebSocketAuthInterceptor验证逻辑
        "username": username               // 附加用户标识
    };

    stompClient.connect(headers,
        (frame) => {
            setConnected(true);
            console.log('认证成功:', frame);

            // 订阅公共频道（与ChatControllerTest测试用例匹配）
            stompClient.subscribe('/chatroom/public', (message) => {
                const msg = JSON.parse(message.body);
                showGreeting(`[公聊] ${msg.senderName}: ${msg.message}`);
            });

            // 订阅私有频道（与WebSocketConfig配置匹配）
            stompClient.subscribe(`/user/chatroom/private`, (message) => {
                const msg = JSON.parse(message.body);
                showGreeting(`[私信] ${msg.senderName}: ${msg.message}`);
            });
        },
        (error) => {
            console.error('connect failed', error);
            alert("认证过期，请重新登录");
            //disconnect();
            //localStorage.clear(); // 清理失效凭证
            //window.location.reload();
        }
    );
}

function sendName() {
    if (!stompClient || !stompClient.connected) {
        alert("请先建立连接");
        return;
    }

    // 构建符合Message对象结构的数据
    const message = {
        senderName: localStorage.getItem("chat-username"),
        receiverName: "public", // 默认公聊接收方
        message: $("#name").val(),
        image: null
    };

    // 根据消息类型选择发送路径
    const destination = message.receiverName === "public"
        ? "/app/message"
        : "/app/private-message";

    stompClient.send(destination, {}, JSON.stringify(message));
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });

});

// 其他函数保持不变...
