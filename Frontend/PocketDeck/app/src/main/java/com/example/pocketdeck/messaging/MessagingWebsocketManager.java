package com.example.pocketdeck.messaging;

public class MessagingWebsocketManager {

    // Singleton instance stuff
    private static MessagingWebsocketManager instance;

    private MessagingWebsocketManager() {}

    public static MessagingWebsocketManager getInstance() {
        if (instance == null) {
            instance = new MessagingWebsocketManager();
        }
        return instance;
    }
}
