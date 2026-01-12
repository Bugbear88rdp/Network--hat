package com.chat.client;

import com.chat.model.Message;
import com.chat.util.ChatLogger;

import java.io.*;


public class ClientReadThread implements Runnable {
    private BufferedReader in;
    private ChatLogger logger;
    private volatile boolean isRunning = true;

    public ClientReadThread(InputStream inputStream, ChatLogger logger) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null && isRunning) {
                try {
                    Message message = Message.fromJson(line);
                    displayMessage(message);

                    if (message.getType() != Message.MessageType.USER_LIST) {
                        logger.log(message.toLogString());
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing message: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    private void displayMessage(Message message) {
        switch (message.getType()) {
            case MESSAGE:
                System.out.println("\n[" + message.getTimestamp() + "] " + message.getUsername() + ": " + message.getContent());
                break;

            case CONNECT:
                System.out.println("\n>>> " + message.getUsername() + " has joined the chat");
                break;

            case DISCONNECT:
                System.out.println("\n<<< " + message.getUsername() + " has left the chat");
                break;

            case USER_LIST:
                System.out.println("\n" + message.getContent());
                break;

            case SYSTEM:
                System.out.println("\n[SYSTEM] " + message.getContent());
                break;

            default:
                System.out.println("\n[" + message.getType() + "] " + message.getContent());
        }

        // Печатаем приглашение ввода
        System.out.print("> ");
    }

    public void stop() {
        isRunning = false;
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing reader: " + e.getMessage());
        }
    }
}