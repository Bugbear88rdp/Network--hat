package com.chat.server;

import com.chat.model.Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//Пул клиентов - управление всеми подключенными клиентами

public class ClientPool {
    private Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public synchronized void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        System.out.println("[ClientPool] Added client: " + username +
                " (Total: " + clients.size() + ")");
    }

    public synchronized void removeClient(String username) {
        clients.remove(username);
        System.out.println("[ClientPool] Removed client: " + username +
                " (Total: " + clients.size() + ")");
    }

    public ClientHandler getClient(String username) {
        return clients.get(username);
    }

    public boolean hasClient(String username) {
        return clients.containsKey(username);
    }

    public List<String> getActiveUsers() {
        return new ArrayList<>(clients.keySet());
    }


    public int getClientCount() {
        return clients.size();
    }


    public void broadcastMessage(Message message) {
        for (ClientHandler client : clients.values()) {
            if (client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }

    public void broadcastMessageExcept(Message message, String excludeUsername) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeUsername) && entry.getValue().isConnected()) {
                entry.getValue().sendMessage(message);
            }
        }
    }

    public boolean sendPrivateMessage(String toUsername, Message message) {
        ClientHandler client = clients.get(toUsername);
        if (client != null && client.isConnected()) {
            client.sendMessage(message);
            return true;
        }
        return false;
    }

    public void clear() {
        clients.clear();
    }

    public String getPoolInfo() {
        return "ClientPool{size=" + clients.size() + ", clients=" + clients.keySet() + "}";
    }
}