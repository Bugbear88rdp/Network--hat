package com.chat.util;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;


public class ConfigLoaderTest {

    private ConfigLoader configLoader;

    @Before
    public void setUp() {
        configLoader = new ConfigLoader();
    }

    @Test
    public void testConfigLoading() {
        assertNotNull(configLoader);
    }

    @Test
    public void testGetServerPort() {
        int port = configLoader.getServerPort();
        assertTrue(port > 0);
        assertTrue(port < 65536);
    }

    @Test
    public void testGetServerHost() {
        String host = configLoader.getServerHost();
        assertNotNull(host);
        assertFalse(host.isEmpty());
    }

    @Test
    public void testGetClientServerHost() {
        String host = configLoader.getClientServerHost();
        assertNotNull(host);
        assertFalse(host.isEmpty());
    }

    @Test
    public void testGetClientServerPort() {
        int port = configLoader.getClientServerPort();
        assertTrue(port > 0);
        assertTrue(port < 65536);
    }

    @Test
    public void testGetLogFile() {
        String logFile = configLoader.getLogFile();
        assertNotNull(logFile);
        assertTrue(logFile.endsWith(".log"));
    }

    @Test
    public void testGetLogDirectory() {
        String directory = configLoader.getLogDirectory();
        assertNotNull(directory);
        assertFalse(directory.isEmpty());
    }

    @Test
    public void testGetFullLogPath() {
        String fullPath = configLoader.getFullLogPath();
        assertNotNull(fullPath);
        assertTrue(fullPath.contains(configLoader.getLogFile()));
    }

    @Test
    public void testGetProperty() {
        String port = configLoader.getProperty("server.port");
        assertNotNull(port);

        String nonExistent = configLoader.getProperty("non.existent.property");
        assertNull(nonExistent);
    }

    @Test
    public void testGetPropertyWithDefault() {
        String defaultValue = "default";
        String result = configLoader.getProperty("non.existent.property", defaultValue);
        assertEquals(defaultValue, result);
    }
}