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



		$.ajax({
			url : 'ws/getSubject',
			dataType : 'json',
			headers : {
				"X-Requested-With" : "XMLHttpRequest"
			},
			success : function(data) {
			     console.log(data)

				    var socket = new SockJS('/ws');
                    stompClient = Stomp.over(socket);
                    //认证
                    stompClient.connect({"X-Authorization": "Bearer " + data.accessToken}, function (frame) {
                        setConnected(true);
                        console.log('Connected: ' + frame);
                        stompClient.subscribe('/chatroom/public', function (greeting) {
                            showGreeting(JSON.parse(greeting.body).message);
                        });
                    });
			},
			error : function(response) {
				if (response.status == 401 || response.status == 402) {
					var loginUrl = response.getResponseHeader("loginurl");
					if (loginUrl) {
						var backUrl = window.location.hash;
						var localBaseIndexOf = loginUrl
								.indexOf("redirect_uri=");
						if (localBaseIndexOf != -1) {
							var localBase = loginUrl.substring(
									localBaseIndexOf + 13, loginUrl
											.indexOf("oauth2-login"));
							backUrl = window.location.href.replace(localBase,
									"");
						}
						//跳转到系统登录页面
						window.location.href = loginUrl + "?backUrl="
								+ encodeURIComponent(backUrl);
					} else {
						window.location.reload(true);
					}
				}
			}
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


    		$.ajax({
    			url : 'ws/getSubject',
    			dataType : 'json',
    			headers : {
    				"X-Requested-With" : "XMLHttpRequest"
    			},
    			success : function(data) {
    			     console.log(data)
    			},
    			error : function(response) {
    				if (response.status == 401 || response.status == 402) {
    					var loginUrl = response.getResponseHeader("loginurl");
    					if (loginUrl) {
    						var backUrl = window.location.hash;
    						var localBaseIndexOf = loginUrl
    								.indexOf("redirect_uri=");
    						if (localBaseIndexOf != -1) {
    							var localBase = loginUrl.substring(
    									localBaseIndexOf + 13, loginUrl
    											.indexOf("oauth2-login"));
    							backUrl = window.location.href.replace(localBase,
    									"");
    						}
    						//跳转到系统登录页面
    						window.location.href = loginUrl + "?backUrl="
    								+ encodeURIComponent(backUrl);
    					} else {
    						window.location.reload(true);
    					}
    				}
    			}
    		});
});

