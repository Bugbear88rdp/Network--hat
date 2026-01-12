package com.chat.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//Загрузчик конфигурации из settings.txt

public class ConfigLoader {
    private static final String CONFIG_FILE = "settings.txt";
    private static final String DEFAULT_SERVER_HOST = "0.0.0.0";
    private static final int DEFAULT_SERVER_PORT = 9090;
    private static final String DEFAULT_LOG_FILE = "file.log";
    private static final String DEFAULT_LOG_DIRECTORY = "./logs";
    private static final String DEFAULT_CLIENT_HOST = "localhost";
    private static final int DEFAULT_CLIENT_PORT = 9090;

    private Map<String, String> config;

    public ConfigLoader() {
        this.config = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        config.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read config file " + CONFIG_FILE);
            System.err.println("Using default configuration...");
        }
    }

    public String getServerHost() {
        return config.getOrDefault("server.host", DEFAULT_SERVER_HOST);
    }

    public int getServerPort() {
        String port = config.get("server.port");
        try {
            return port != null ? Integer.parseInt(port) : DEFAULT_SERVER_PORT;
        } catch (NumberFormatException e) {
            return DEFAULT_SERVER_PORT;
        }
    }

    public String getClientServerHost() {
        return config.getOrDefault("client.server.host", DEFAULT_CLIENT_HOST);
    }

    public int getClientServerPort() {
        String port = config.get("client.server.port");
        try {
            return port != null ? Integer.parseInt(port) : DEFAULT_CLIENT_PORT;
        } catch (NumberFormatException e) {
            return DEFAULT_CLIENT_PORT;
        }
    }

    public String getLogFile() {
        return config.getOrDefault("log.file", DEFAULT_LOG_FILE);
    }

    public String getLogDirectory() {
        return config.getOrDefault("log.directory", DEFAULT_LOG_DIRECTORY);
    }

    public String getFullLogPath() {
        String directory = getLogDirectory();
        String fileName = getLogFile();
        if (directory.endsWith("/") || directory.endsWith("\\")) {
            return directory + fileName;
        }
        return directory + "/" + fileName;
    }

    public String getProperty(String key) {
        return config.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    @Override
    public String toString() {
        return "ConfigLoader{" +
                "config=" + config +
                '}';
    }
}