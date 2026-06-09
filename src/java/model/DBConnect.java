package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    //coordinate database locale
    private static final String URL = "jdbc:mysql://localhost:3306/webdelivery?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
   
    private static final String PASSWORD = "pass123"; 

    public static Connection getConnection() throws SQLException {
        try {
            // Chiama il driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Errore: Driver JDBC non trovato. Controlla le Libraries!", e);
        }
    }
}