var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {

				    var socket = new SockJS('/ws');
                    stompClient = Stomp.over(socket);
                    //认证
                    stompClient.connect({"X-Authorization": "Bearer " + "xx"}, function (frame) {
                        setConnected(true);
                        console.log('Connected: ' + frame);
                        stompClient.subscribe('/chatroom/public', function (greeting) {
                            showGreeting(JSON.parse(greeting.body).message);
                        });
                    });


}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    var image;
    stompClient.send("/app/message", {}
    , JSON.stringify({
             senderName: localStorage.getItem("chat-username"),
             message: $("#name").val(),
             image: image,
           })
    );
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

