package com.chat.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum MessageType {
        CONNECT,      // Пользователь подключился
        MESSAGE,      // Обычное сообщение
        DISCONNECT,   // Пользователь отключился
        USER_LIST,    // Список пользователей
        PING,         // Проверка соединения
        SYSTEM        // Системное сообщение
    }

    private MessageType type;
    private String username;
    private String content;
    private String timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now().format(formatter);
    }


    public Message(MessageType type, String username, String content) {
        this.type = type;
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    public Message(MessageType type, String username, String content, String timestamp) {
        this.type = type;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }


    //getters, setters
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    //сериализация из json
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    //десериализация из json

    public static Message fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, username, content);
    }

    //Для логирования

    public String toLogString() {
        return String.format("[%s] [%s] %s: %s", timestamp, type, username, content);
    }
}