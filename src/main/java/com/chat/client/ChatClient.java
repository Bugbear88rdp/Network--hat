package com.chat.client;

import com.chat.model.Message;
import com.chat.util.ChatLogger;
import com.chat.util.ConfigLoader;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient {
    private Socket socket;
    private ChatLogger logger;
    private ConfigLoader config;
    private String username;
    private ClientInputThread inputThread;
    private ClientReadThread readThread;
    private Thread inputThreadHandle;
    private Thread readThreadHandle;

    public ChatClient() {
        this.config = new ConfigLoader();
        this.logger = new ChatLogger(config.getFullLogPath());
    }


    public void start() {
        try {
            this.username = getUserInput("Enter your username: ");

            String serverHost = config.getClientServerHost();
            int serverPort = config.getClientServerPort();

            System.out.println("Connecting to " + serverHost + ":" + serverPort + "...");

            socket = new Socket(serverHost, serverPort);

            logger.logConnected(username);
            System.out.println("Connected to server!");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Message connectMsg = new Message(
                    Message.MessageType.CONNECT,
                    username,
                    "User has connected"
            );
            out.println(connectMsg.toJson());
            out.flush();

            startThreads();

            waitForCompletion();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            logger.logSystem("Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private String getUserInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        while (input.isEmpty()) {
            System.out.print("Username cannot be empty. Try again: ");
            input = scanner.nextLine().trim();
        }

        return input;
    }

    private void startThreads() {
        try {
            readThread = new ClientReadThread(
                    socket.getInputStream(),
                    logger
            );
            readThreadHandle = new Thread(readThread, "ClientReadThread");
            readThreadHandle.setDaemon(false);
            readThreadHandle.start();

            inputThread = new ClientInputThread(
                    socket.getOutputStream(),
                    username,
                    logger
            );
            inputThreadHandle = new Thread(inputThread, "ClientInputThread");
            inputThreadHandle.setDaemon(false);
            inputThreadHandle.start();

        } catch (IOException e) {
            System.err.println("Error starting threads: " + e.getMessage());
        }
    }

    private void waitForCompletion() {
        try {
            if (inputThreadHandle != null) {
                inputThreadHandle.join();
            }
            if (readThreadHandle != null) {
                readThreadHandle.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void cleanup() {
        System.out.println("\nDisconnecting...");


        if (inputThread != null) {
            inputThread.stop();
        }
        if (readThread != null) {
            readThread.stop();
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }

        System.out.println("Disconnected from server");
        logger.logSystem("Client disconnected");
    }


    public static void main(String[] args) {
        System.out.println("---------------------------------------");
        System.out.println("   NETWORK CHAT CLIENT");
        System.out.println("---------------------------------------");

        ChatClient client = new ChatClient();
        client.start();
    }
}