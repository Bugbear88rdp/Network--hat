package com.chat.model;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class MessageTest {

    private Message message;

    @Before
    public void setUp() {
        message = new Message(
                Message.MessageType.MESSAGE,
                "TestUser",
                "Hello World"
        );
    }

    @Test
    public void testMessageCreation() {
        assertEquals(Message.MessageType.MESSAGE, message.getType());
        assertEquals("TestUser", message.getUsername());
        assertEquals("Hello World", message.getContent());
        assertNotNull(message.getTimestamp());
    }

    @Test
    public void testMessageTypes() {
        Message connectMsg = new Message(
                Message.MessageType.CONNECT,
                "User1",
                "Connected"
        );
        assertEquals(Message.MessageType.CONNECT, connectMsg.getType());

        Message disconnectMsg = new Message(
                Message.MessageType.DISCONNECT,
                "User1",
                "Disconnected"
        );
        assertEquals(Message.MessageType.DISCONNECT, disconnectMsg.getType());
    }

    @Test
    public void testJsonSerialization() {
        String json = message.toJson();
        assertNotNull(json);
        assertTrue(json.contains("TestUser"));
        assertTrue(json.contains("Hello World"));
        assertTrue(json.contains("MESSAGE"));
    }

    @Test
    public void testJsonDeserialization() {
        String json = message.toJson();
        Message deserializedMsg = Message.fromJson(json);

        assertEquals(message.getType(), deserializedMsg.getType());
        assertEquals(message.getUsername(), deserializedMsg.getUsername());
        assertEquals(message.getContent(), deserializedMsg.getContent());
    }

    @Test
    public void testMessageToString() {
        String str = message.toString();
        assertNotNull(str);
        assertTrue(str.contains("TestUser"));
        assertTrue(str.contains("Hello World"));
    }

    @Test
    public void testMessageWithTimestamp() {
        String customTimestamp = "2026-01-11 23:00:00";
        Message msg = new Message(
                Message.MessageType.MESSAGE,
                "User",
                "Test",
                customTimestamp
        );
        assertEquals(customTimestamp, msg.getTimestamp());
    }

    @Test
    public void testSetters() {
        message.setType(Message.MessageType.SYSTEM);
        message.setUsername("NewUser");
        message.setContent("New content");
        message.setTimestamp("2026-01-11 20:00:00");

        assertEquals(Message.MessageType.SYSTEM, message.getType());
        assertEquals("NewUser", message.getUsername());
        assertEquals("New content", message.getContent());
        assertEquals("2026-01-11 20:00:00", message.getTimestamp());
    }
}