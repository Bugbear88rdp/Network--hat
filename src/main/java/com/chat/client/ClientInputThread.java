package com.chat.client;

import com.chat.model.Message;
import com.chat.util.ChatLogger;

import java.io.*;


public class ClientInputThread implements Runnable {
    private PrintWriter out;
    private ChatLogger logger;
    private String username;
    private volatile boolean isRunning = true;

    public ClientInputThread(OutputStream outputStream, String username, ChatLogger logger) {
        this.out = new PrintWriter(outputStream, true);
        this.username = username;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;

            System.out.println("\nWelcome to Network Chat!");
            System.out.println("Type your messages and press Enter to send");
            System.out.println("Type '/exit' to leave the chat\n");
            System.out.print("> ");

            while ((userInput = consoleReader.readLine()) != null && isRunning) {

                if (userInput.trim().equals("/exit")) {
                    handleExit();
                    break;
                }

                if (userInput.trim().isEmpty()) {
                    System.out.print("> ");
                    continue;
                }

                Message message = new Message(
                        Message.MessageType.MESSAGE,
                        username,
                        userInput
                );

                out.println(message.toJson());
                out.flush();

                logger.logMessage(username, userInput);

                System.out.print("> ");
            }

        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Input error: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    private void handleExit() {
        System.out.println("\nSending disconnect message...");

        Message disconnectMsg = new Message(
                Message.MessageType.DISCONNECT,
                username,
                "User is leaving"
        );

        out.println(disconnectMsg.toJson());
        out.flush();

        logger.logDisconnected(username);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        isRunning = false;
        if (out != null) {
            out.close();
        }
    }
}