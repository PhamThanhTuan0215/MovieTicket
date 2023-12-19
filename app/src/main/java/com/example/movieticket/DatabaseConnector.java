package com.example.movieticket;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String DB_URL = "mysql://utshen5o2j5yonof:46qx3PxdaGjzpmwR8pbg@bssymz5sty8ih8hrbmi6-mysql.services.clever-cloud.com:3306/bssymz5sty8ih8hrbmi6";
    private static final String USER = "utshen5o2j5yonof";
    private static final String PASS = "46qx3PxdaGjzpmwR8pbg";

    public DatabaseConnector() {}

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
//            return DriverManager.getConnection("jdbc:mysql://localhost:3306/movieticket", "root", "");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
}
