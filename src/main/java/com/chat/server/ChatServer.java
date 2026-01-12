package com.chat.server;

import com.chat.util.ChatLogger;
import com.chat.util.ConfigLoader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


// Главный класс сервера чата
// Слушает входящие подключения и создает обработчик для каждого клиента

public class ChatServer {
    private ServerSocket serverSocket;
    private ChatLogger logger;
    private ConfigLoader config;
    private ClientPool clientPool;
    private ExecutorService threadPool;
    private volatile boolean isRunning = false;

    private static final int THREAD_POOL_SIZE = 10;

    public ChatServer() {
        this.config = new ConfigLoader();
        this.logger = new ChatLogger(config.getFullLogPath());
        this.clientPool = new ClientPool();
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    //запуск сурвера

    public void start() {
        try {
            int port = config.getServerPort();
            String host = config.getServerHost();

            serverSocket = new ServerSocket(port);
            isRunning = true;

            logger.logSystem("Server started on " + host + ":" + port);
            System.out.println("---------------------------------------");
            System.out.println("   NETWORK CHAT SERVER");
            System.out.println("---------------------------------------");
            System.out.println("Server started on: " + host + ":" + port);
            System.out.println("Log file: " + config.getFullLogPath());
            System.out.println("Waiting for connections...");
            System.out.println("_______________________________________");

            // основной цикл прослушивания входящих подключений
            acceptConnections();

        } catch (IOException e) {
            logger.logSystem("Failed to start server: " + e.getMessage());
            System.err.println("Failed to start server: " + e.getMessage());
            isRunning = false;
        } finally {
            shutdown();
        }
    }


    //Цикл принятия входящих подключений

    private void acceptConnections() {
        try {
            while (isRunning) {
                try {
                    // Слушаем входящие подключения
                    Socket clientSocket = serverSocket.accept();

                    // Создаем обработчик клиента и запускаем его в отдельном потоке
                    ClientHandler clientHandler = new ClientHandler(
                            clientSocket,
                            clientPool,
                            logger
                    );

                    threadPool.execute(clientHandler);

                } catch (SocketException e) {
                    // Нормальное завершение при shutdown
                    if (isRunning) {
                        System.err.println("Socket exception: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in acceptConnections: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Корректно завершить работу сервера

    public void shutdown() {
        System.out.println("\n[Server] Shutting down...");
        logger.logSystem("Server shutting down");

        isRunning = false;

        try {
            // Закрываем ServerSocket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // Закрываем все клиентские соединения
            clientPool.clear();

            // Завершаем thread pool
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }

            logger.logSystem("Server stopped");
            System.out.println("[Server] Shutdown complete");

        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    //Получить информацию о сервере

    public String getServerInfo() {
        return "ChatServer{" +
                "port=" + config.getServerPort() +
                ", activeClients=" + clientPool.getClientCount() +
                ", isRunning=" + isRunning +
                '}';
    }

    //Точка входа

    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        // Добавляем shutdown hook для корректного завершения при Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        server.start();
    }
}