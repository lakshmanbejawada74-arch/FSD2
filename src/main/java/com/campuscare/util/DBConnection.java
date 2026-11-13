package com.campuscare.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Reusable JDBC Connection manager for CampusCare.
 * Loads configuration securely from db.properties in the classpath.
 */
public class DBConnection {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/campuscare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "1234");
            } else {
                // Fallback default values
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/campuscare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                username = "root";
                password = "1234";
            }
            Class.forName(driver);
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to load db.properties or register MySQL Driver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection.
     * Callers must close connections with try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (driver != null) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found on classpath: " + driver, e);
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Helper to close auto-closeable resources safely without throwing checked exceptions.
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
