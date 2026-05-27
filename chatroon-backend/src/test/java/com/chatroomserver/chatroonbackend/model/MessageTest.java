package com.chatroomserver.chatroonbackend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    @Test
    public void testToString() {
        Message message = new Message();
        message.setSenderName("Alice");
        message.setReceiverName("Bob");
        message.setMessage("Hello, World!");
        message.setMedia("image.png");
        message.setStatus(Status.MESSAGE);
        message.setMediaType("image/png");

        String toStringResult = message.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("senderName=Alice"));
        assertTrue(toStringResult.contains("receiverName=Bob"));
        assertTrue(toStringResult.contains("message=Hello, World!"));
        assertTrue(toStringResult.contains("media=image.png"));
        assertTrue(toStringResult.contains("status=MESSAGE"));
        assertTrue(toStringResult.contains("mediaType=image/png"));
    }
}
