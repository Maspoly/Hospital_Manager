package br.edu.ufersa.hospital_manager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private final static String URL = "jdbc:mysql://localhost:3306/hospital_manager";
    private final static String USER = "root";
    private final static String PASS = "192106";
    private static Connection connection = null;

    private Connector() {
        // Private constructor to prevent instantiation
    }

    // Singleton pattern for database connection
    public static Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()){
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e){
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }

    public static boolean isAvailable() {
        return getConnection() != null;
    }

    // Method to close the connection when done
    public static void closeConnection(){
        if (connection !=  null){
            try{
                connection.close();
            }catch (SQLException e){e.printStackTrace();}
        }
    }
}
