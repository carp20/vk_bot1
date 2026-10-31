package com.carp2;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class HelloBot extends LongPollBot {
    @Override
    public void onMessageNew(MessageNew messageNew) {
        try {
            Message message = messageNew.getMessage();
            if (message.hasText()) {
                String response = "Привет! Получено твоё сообщение: " + message.getText();
                vk.messages.send()
                        .setPeerId(message.getPeerId())
                        .setMessage(response)
                        .execute();
            }
        } catch (VkApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAccessToken() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(".private/config.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props.getProperty("access_token");
    }

    public static void main(String[] args) throws VkApiException {
        new HelloBot().startPolling();
    }
}