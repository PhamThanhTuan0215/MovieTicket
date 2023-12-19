package com.example.movieticket;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseExecutor {
    public static String executeQuery() {
        try {
            Connection connection = DatabaseConnector.connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM movie");

            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                String data = resultSet.getString("name");
                result.append(data).append("\n");
            }

            resultSet.close();
            statement.close();
            return result.toString();
        } catch (SQLException e) {
            Log.e("Error", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
