package com.example.pocketdeck.messaging;

public class Message {
    // Display name of the sender
    private String senderName;
    // Message contents
    private String message;

    public Message(String senderName, String message) {
        // Construct message object
        this.senderName = senderName;
        this.message = message;
        // Create UI object
    }

    public String getUsername() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }
}