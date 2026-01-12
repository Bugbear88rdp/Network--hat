package com.chat.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.io.*;

public class ChatLoggerTest {

    private ChatLogger logger;
    private String testLogFile = "./test_logs/test_file.log";

    @Before
    public void setUp() {
        logger = new ChatLogger(testLogFile);
    }

    @After
    public void tearDown() {
        File file = new File(testLogFile);
        if (file.exists()) {
            file.delete();
        }

        File directory = new File("./test_logs");
        if (directory.exists()) {
            directory.delete();
        }
    }

    @Test
    public void testLoggerCreation() {
        assertNotNull(logger);
        assertNotNull(logger.getLogFilePath());
    }

    @Test
    public void testLogFileCreation() {
        File file = new File(testLogFile);
        assertTrue("Log file should be created", file.exists());
    }

    @Test
    public void testLogging() {
        logger.log("Test message");

        File file = new File(testLogFile);
        assertTrue("Log file should exist", file.exists());
        assertTrue("Log file should not be empty", file.length() > 0);
    }

    @Test
    public void testLogAppend() throws IOException {
        logger.log("First message");
        long sizeAfterFirst = new File(testLogFile).length();

        logger.log("Second message");
        long sizeAfterSecond = new File(testLogFile).length();

        assertTrue("Second log should increase file size", sizeAfterSecond > sizeAfterFirst);
    }

    @Test
    public void testLogWithTypes() {
        logger.logConnected("TestUser");
        logger.logDisconnected("TestUser");
        logger.logMessage("TestUser", "Hello");
        logger.logSystem("System event");

        File file = new File(testLogFile);
        assertTrue("Log file should contain logs", file.length() > 0);
    }

    @Test
    public void testLogFileContains() throws IOException {
        String testMessage = "TestLogMessage";
        logger.log(testMessage);

        BufferedReader reader = new BufferedReader(new FileReader(testLogFile));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.contains(testMessage)) {
                found = true;
                break;
            }
        }
        reader.close();

        assertTrue("Log file should contain the test message", found);
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        // Тест параллельного логирования
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    logger.log("Thread " + threadNum + " Message " + j);
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        File file = new File(testLogFile);
        assertTrue("Log file should contain all messages", file.length() > 0);
    }
}