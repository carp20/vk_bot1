package com.carp2;

import java.sql.*;

public class WorkWithDB {
    public static boolean exec_db(String db_url, String prompt) {
        try (Connection conn = DriverManager.getConnection(db_url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database");

                Statement stmt = conn.createStatement();
                stmt.execute(prompt);

//                String insertSQL = "INSERT INTO users (name) VALUES ('Alice')";
//                stmt.execute(insertSQL);
//
//                ResultSet resultSet = stmt.executeQuery("SELECT * FROM users");
//                while (resultSet.next()) {
//                    System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
//                }
                conn.close();
                return true;
            } else {
                System.out.println("Conn is null");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static String getFromDB(String db_url, String prompt){
        try (Connection conn = DriverManager.getConnection(db_url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database");

                Statement stmt = conn.createStatement();
                ResultSet toReturn = stmt.executeQuery(prompt);
                String answer = toReturn.getString("name");
                conn.close();
                return answer;
            } else {
                System.out.println("Conn is null");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
