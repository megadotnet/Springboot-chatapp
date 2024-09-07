package com.chatroomserver.chatroonbackend.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chatroomserver.chatroonbackend.model.Message;
import com.chatroomserver.chatroonbackend.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ChatController.class})
@ExtendWith(SpringExtension.class)
class ChatControllerDiffblueTest {
    @Autowired
    private ChatController chatController;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Method under test: {@link ChatController#receiveMessage(Message)}
     */
    @Test
    void testReceiveMessage() throws InterruptedException {
        Message message = new Message("Sender Name", "Receiver Name", "Not all who wander are lost", "Media", Status.JOIN,
                "Media Type");

        assertSame(message, chatController.receiveMessage(message));
    }

    /**
     * Method under test: {@link ChatController#receiveMessage(Message)}
     */
    @Test
    void testReceiveMessage2() throws InterruptedException {
        Message message = mock(Message.class);
        when(message.getMessage()).thenReturn("Not all who wander are lost");
        Message actualReceiveMessageResult = chatController.receiveMessage(message);
        verify(message).getMessage();
        assertSame(message, actualReceiveMessageResult);
    }

    /**
     * Method under test: {@link ChatController#privateMessage(Message)}
     */
    @Test
    void testPrivateMessage() throws MessagingException {
        doNothing().when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());
        Message message = new Message("Sender Name", "Receiver Name", "Not all who wander are lost", "Media", Status.JOIN,
                "Media Type");

        Message actualPrivateMessageResult = chatController.privateMessage(message);
        verify(simpMessagingTemplate).convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(),
                Mockito.<Object>any());
        assertSame(message, actualPrivateMessageResult);
    }
}
