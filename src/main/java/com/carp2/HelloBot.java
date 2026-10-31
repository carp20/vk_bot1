package com.carp2;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class HelloBot extends LongPollBot {
    static String db_url = "jdbc:sqlite:myapp.db";

    @Override
    public void onMessageNew(MessageNew messageNew) {
        try {
            Message message = messageNew.getMessage();
            if (message.hasText()) {
                if (message.getText().charAt(0) == '/') {
                    String[] mess = message.getText().split(" ");
                    if (mess[0].equals("/work")) {
                        vk.messages.send()
                                .setPeerId(message.getPeerId())
                                .setMessage("Работать?.. Что это такое?..")
                                .execute();
                    } else if (mess[0].equals("/getName")) {
                        String prompt = "SELECT * FROM users WHERE vkID = "+message.getFromId();
                        System.out.println(prompt);
                        String name = WorkWithDB.getFromDB(db_url, prompt);
                        vk.messages.send()
                                .setPeerId(message.getPeerId())
                                .setMessage("Имя в боте: "+name)
                                .execute();
                    } else if (mess[0].equals("/addName")) {
                        String prompt = "INSERT INTO users (vkID, name) VALUES ("+message.getFromId()+", '"+mess[1]+"')";
                        WorkWithDB.exec_db(db_url, prompt);
                    } else if (mess[0].equals("/setName")) {
                        String prompt = "UPDATE users SET name='"+mess[1]+"' WHERE vkID="+message.getFromId();
                        WorkWithDB.exec_db(db_url, prompt);
                    } else {
                        vk.messages.send()
                                .setPeerId(message.getPeerId())
                                .setMessage("Команда не распознана")
                                .execute();
                    }
                } else {
                    String response = "Привет! Получено твоё сообщение: " + message.getText();
                    vk.messages.send()
                            .setPeerId(message.getPeerId())
                            .setMessage(response)
                            .execute();
                }
            } else {
                vk.messages.send()
                        .setPeerId(message.getPeerId())
                        .setMessage("Текст в сообщении не найден")
                        .execute();
            }
            System.out.println("Сообщение от "+message.getPeerId()+" (fromID: "+message.getFromId()+") , текст сообщения: "+message.getText());
        } catch (VkApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAccessToken() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(".private/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props.getProperty("access_token");
    }

    public static void main(String[] args) throws VkApiException {
        System.out.println(new java.io.File("myapp.db").getAbsolutePath());
        try (Connection conn = DriverManager.getConnection(db_url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database");

                String createTableSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, vkID INTEGER, name TEXT)";
                Statement stmt = conn.createStatement();
                stmt.execute(createTableSQL);
                conn.close();
            }
            else{
                System.out.println("Conn is null");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        new HelloBot().startPolling();
    }
}