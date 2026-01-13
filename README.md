# Network Chat

Многопользовательское консольное приложение для обмена текстовыми сообщениями по сети, разработанное на Java.

## Описание проекта

Network Chat - это курсовой проект, представляющий собой систему для обмена сообщениями между несколькими пользователями через сокеты TCP/IP. Проект состоит из двух приложений:

- **Сервер** (ChatServer) - управляет подключениями клиентов и рассылает сообщения
- **Клиент** (ChatClient) - подключается к серверу и взаимодействует с другими пользователями

##  Возможности

-  Многопоточный сервер на Java
-  Поддержка одновременного подключения нескольких клиентов
-  Обмен текстовыми сообщениями в реальном времени
-  Логирование всех сообщений в файл (file.log)
-  Конфигурация через файл settings.txt
-  JSON-протокол обмена сообщениями
-  Unit-тесты всех компонентов
-  Graceful shutdown сервера и клиента

## Архитектура

### Структура потоков сервера:

```
ChatServer (основной поток)
├── Accept Thread (принимает подключения)
├── ClientHandler 1 (обработка клиента 1) - отдельный поток
├── ClientHandler 2 (обработка клиента 2) - отдельный поток
└── ClientHandler N (обработка клиента N) - отдельный поток
```

Каждый ClientHandler работает в отдельном потоке для одновременной обработки нескольких клиентов.

### Структура потоков клиента:

```
ChatClient
├── ClientInputThread (чтение ввода с консоли)
└── ClientReadThread (чтение сообщений с сервера)
```

## Технологический стек

- **Java 11+**
- **Gradle** - build tool
- **JUnit 4** - unit testing
- **GSON** - JSON serialization
- **SLF4J** - logging

## Структура проекта

```
network-chat/
├── src/
│   ├── main/java/com/chat/
│   │   ├── server/
│   │   │   ├── ChatServer.java
│   │   │   ├── ClientHandler.java
│   │   │   └── ClientPool.java
│   │   ├── client/
│   │   │   ├── ChatClient.java
│   │   │   ├── ClientInputThread.java
│   │   │   └── ClientReadThread.java
│   │   ├── model/
│   │   │   └── Message.java
│   │   └── util/
│   │       ├── ChatLogger.java
│   │       └── ConfigLoader.java
│   └── test/java/com/chat/
│       ├── model/
│       │   └── MessageTest.java
│       ├── server/
│       │   └── ClientPoolTest.java
│       └── util/
│           ├── ChatLoggerTest.java
│           └── ConfigLoaderTest.java
├── settings.txt
├── build.gradle
├── README.md
└── .gitignore
```

## Конфигурация

Все настройки находятся в файле `settings.txt`:

```properties
# Server settings
server.port=9090
server.host=0.0.0.0

# Logging settings
log.file=file.log
log.directory=./logs

# Client settings
client.server.host=localhost
client.server.port=9090
```

### Параметры:

- `server.port` - порт сервера (default: 9090)
- `server.host` - адрес сервера (default: 0.0.0.0)
- `log.file` - имя файла логов (default: file.log)
- `log.directory` - директория логов (default: ./logs)
- `client.server.host` - адрес сервера для подключения клиента
- `client.server.port` - порт сервера для подключения клиента

## Использование

### Взаимодействие с клиентом:

1. Запустите сервер
2. Запустите один или несколько клиентов
3. Введите имя пользователя
4. Начните писать сообщения
5. Для выхода введите `/exit`


##  Протокол обмена сообщениями

Все сообщения передаются в формате JSON:

```json
{
  "type": "MESSAGE",
  "username": "Alice",
  "content": "Hello World",
  "timestamp": "2026-01-11 23:10:00"
}
```

### Типы сообщений:

- `CONNECT` - пользователь подключился
- `MESSAGE` - обычное сообщение
- `DISCONNECT` - пользователь отключился
- `USER_LIST` - список активных пользователей
- `SYSTEM` - системное сообщение

## Тестирование

### Запуск всех тестов:

```bash
gradle test
```

### Запуск конкретного теста:

```bash
gradle test --tests MessageTest
gradle test --tests ClientPoolTest
gradle test --tests ChatLoggerTest
gradle test --tests ConfigLoaderTest
```

### Покрытие тестами:

Проект включает unit-тесты для:
-  Message (сериализация/десериализация, типы)
-  ConfigLoader (загрузка конфигурации)
-  ChatLogger (логирование, потокобезопасность)
-  ClientPool (управление клиентами, рассылка)

## Интеграционные тесты

### Тест сервера с telnet:

```bash
# Терминал 1
gradle runServer

# Терминал 2
telnet localhost 9090
```

### Тест с несколькими клиентами:

```bash
# Терминал 1
gradle runServer

# Терминал 2
gradle runClient

# Терминал 3
gradle runClient

# Терминал 4
gradle runClient
```

## Классы и методы

### ChatServer

```java
public class ChatServer {
    public void start()           // Запустить сервер
    public void shutdown()        // Завершить работу сервера
    public String getServerInfo() // Получить информацию
}
```

### ClientHandler

```java
public class ClientHandler implements Runnable {
    public void run()                           // Основной цикл обработки
    public void sendMessage(Message message)    // Отправить сообщение
    public boolean isConnected()                // Проверить соединение
}
```

### ChatClient

```java
public class ChatClient {
    public void start()     // Запустить клиент
    public void cleanup()   // Очистить ресурсы
}
```

### Message

```java
public class Message {
    public String toJson()                      // В JSON
    public static Message fromJson(String json) // Из JSON
    public String toString()                    // В строку
}
```
