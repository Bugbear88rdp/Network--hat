package com.chat.server;

import com.chat.model.Message;
import com.chat.util.ChatLogger;

import java.io.*;
import java.net.Socket;
import java.util.*;

//Обработчик подключенного клиента (работает в отдельном потоке)

public class ClientHandler implements Runnable {
    private Socket socket;
    private ClientPool clientPool;
    private ChatLogger logger;
    private String username;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean isConnected = true;

    public ClientHandler(Socket socket, ClientPool clientPool, ChatLogger logger) {
        this.socket = socket;
        this.clientPool = clientPool;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            // Инициализируем потоки ввода-вывода
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ждем сообщения CONNECT с именем пользователя
            String firstMessage = in.readLine();
            if (firstMessage != null) {
                Message msg = Message.fromJson(firstMessage);

                if (msg.getType() == Message.MessageType.CONNECT) {
                    this.username = msg.getUsername();

                    // Добавляем клиента в пул
                    clientPool.addClient(username, this);
                    logger.logConnected(username);

                    System.out.println("[Server] User connected: " + username);

                    // Отправляем подтверждение и список пользователей
                    sendUserList();

                    // Транслируем сообщение о подключении всем клиентам
                    broadcastMessage(msg);

                    // Читаем сообщения от клиента
                    String line;
                    while ((line = in.readLine()) != null && isConnected) {
                        Message incomingMsg = Message.fromJson(line);

                        if (incomingMsg.getType() == Message.MessageType.DISCONNECT) {
                            handleDisconnect();
                            break;
                        } else if (incomingMsg.getType() == Message.MessageType.MESSAGE) {
                            logger.logMessage(username, incomingMsg.getContent());
                            broadcastMessage(incomingMsg);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            handleDisconnect();
        }
    }

    //Обработка отключения клиента

    private void handleDisconnect() {
        if (!isConnected) return;

        isConnected = false;

        try {
            // Удаляем клиента из пула
            clientPool.removeClient(username);
            logger.logDisconnected(username);

            System.out.println("[Server] User disconnected: " + username);

            // Транслируем сообщение об отключении
            Message disconnectMsg = new Message(
                    Message.MessageType.DISCONNECT,
                    username,
                    "User has left the chat"
            );
            broadcastMessage(disconnectMsg);

            // Закрываем соединение
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    //Отправить сообщение клиенту

    public synchronized void sendMessage(String jsonMessage) {
        if (isConnected && out != null) {
            out.println(jsonMessage);
            out.flush();
        }
    }

    //Отправить сообщение Object

    public void sendMessage(Message message) {
        sendMessage(message.toJson());
    }

    //Рассылка сообщения всем клиентам

    private void broadcastMessage(Message message) {
        clientPool.broadcastMessage(message);
    }


    private void sendUserList() {
        List<String> users = clientPool.getActiveUsers();
        StringBuilder userList = new StringBuilder();
        for (String user : users) {
            if (!userList.toString().isEmpty()) {
                userList.append(", ");
            }
            userList.append(user);
        }

        Message userListMsg = new Message(
                Message.MessageType.USER_LIST,
                "SERVER",
                "Active users: " + userList.toString()
        );
        sendMessage(userListMsg);
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return isConnected;
    }
}