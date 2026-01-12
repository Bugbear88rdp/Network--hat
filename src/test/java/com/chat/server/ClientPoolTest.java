package com.chat.server;

import com.chat.model.Message;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class ClientPoolTest {
    
    private ClientPool clientPool;
    private ClientHandler mockHandler1;
    private ClientHandler mockHandler2;
    
    @Before
    public void setUp() {
        clientPool = new ClientPool();
        mockHandler1 = new MockClientHandler();
        mockHandler2 = new MockClientHandler();
    }
    
    @Test
    public void testAddClient() {
        clientPool.addClient("User1", mockHandler1);
        assertTrue("Client should be added", clientPool.hasClient("User1"));
    }
    
    @Test
    public void testRemoveClient() {
        clientPool.addClient("User1", mockHandler1);
        clientPool.removeClient("User1");
        assertFalse("Client should be removed", clientPool.hasClient("User1"));
    }
    
    @Test
    public void testGetClient() {
        clientPool.addClient("User1", mockHandler1);
        ClientHandler retrieved = clientPool.getClient("User1");
        assertNotNull("Client should be retrieved", retrieved);
    }
    
    @Test
    public void testHasClient() {
        clientPool.addClient("User1", mockHandler1);
        assertTrue(clientPool.hasClient("User1"));
        assertFalse(clientPool.hasClient("User2"));
    }
    
    @Test
    public void testGetActiveUsers() {
        clientPool.addClient("User1", mockHandler1);
        clientPool.addClient("User2", mockHandler2);
        
        java.util.List<String> users = clientPool.getActiveUsers();
        assertEquals("Should have 2 users", 2, users.size());
        assertTrue(users.contains("User1"));
        assertTrue(users.contains("User2"));
    }
    
    @Test
    public void testGetClientCount() {
        assertEquals("Initial count should be 0", 0, clientPool.getClientCount());
        
        clientPool.addClient("User1", mockHandler1);
        assertEquals("Count should be 1", 1, clientPool.getClientCount());
        
        clientPool.addClient("User2", mockHandler2);
        assertEquals("Count should be 2", 2, clientPool.getClientCount());
    }
    
    @Test
    public void testBroadcastMessage() {
        clientPool.addClient("User1", mockHandler1);
        clientPool.addClient("User2", mockHandler2);
        
        Message msg = new Message(
            Message.MessageType.MESSAGE,
            "User1",
            "Hello everyone"
        );
        
        // Проверяем что рассылка не выбросит ошибку
        clientPool.broadcastMessage(msg);
    }
    
    @Test
    public void testClear() {
        clientPool.addClient("User1", mockHandler1);
        clientPool.addClient("User2", mockHandler2);
        
        clientPool.clear();
        assertEquals("Pool should be empty", 0, clientPool.getClientCount());
    }

    private static class MockClientHandler extends ClientHandler {
        public MockClientHandler() {
            super(null, null, null);
        }
        
        @Override
        public boolean isConnected() {
            return true;
        }
    }
}