package com.healthcare.util;

import java.sql.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/healthcare_plus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        System.out.println("DEBUG: Attempting database connection to: " + URL);
        try {
            // Test if MySQL driver is available
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("DEBUG: Database connection successful: " + (conn != null ? "CONNECTED" : "FAILED"));
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("DEBUG: MySQL driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found. Please ensure mysql-connector-j.jar is in the classpath.", e);
        } catch (SQLException e) {
            System.err.println("DEBUG: Database connection failed: " + e.getMessage());
            // Check if it's a connection refused error (database not running)
            if (e.getMessage().contains("Connection refused") || e.getMessage().contains("Communications link failure")) {
                System.err.println("DEBUG: Database server may not be running or accessible");
            }
            throw e;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }
}
