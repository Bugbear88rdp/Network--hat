package com.chat.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String logFilePath;
    private Object lockObject = new Object();

    public ChatLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        createLogFile();
    }

    private void createLogFile() {
        try {
            File file = new File(logFilePath);
            File directory = file.getParentFile();

            // Создаем директорию если ее нет
            if (directory != null && !directory.exists()) {
                if (!directory.mkdirs()) {
                    System.err.println("Failed to create log directory: " + directory.getAbsolutePath());
                }
            }

            // Создаем файл если его нет
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    System.err.println("Failed to create log file: " + logFilePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }


    public void log(String message) {
        synchronized (lockObject) {
            try (FileWriter fw = new FileWriter(logFilePath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter pw = new PrintWriter(bw)) {

                String timestamp = LocalDateTime.now().format(formatter);
                pw.println("[" + timestamp + "] " + message);
                pw.flush();

            } catch (IOException e) {
                System.err.println("Error writing to log file: " + e.getMessage());
            }
        }
    }

    public void log(String username, String messageType, String content) {
        String logMessage = String.format("[%s] %s: %s", messageType, username, content);
        log(logMessage);
    }


    public void logConnected(String username) {
        log(username, "CONNECTED", "User connected");
    }

    public void logDisconnected(String username) {
        log(username, "DISCONNECTED", "User disconnected");
    }

    public void logMessage(String username, String content) {
        log(username, "MESSAGE", content);
    }


    public void logSystem(String message) {
        log("SYSTEM", "SYSTEM", message);
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}