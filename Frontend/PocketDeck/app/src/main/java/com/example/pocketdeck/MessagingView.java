package com.example.pocketdeck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MessagingView extends AppCompatActivity {

    private RecyclerView messageView;
    private Message[] messageList;

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_messaging);

        // Get messages array from server / websocket
        // Update display
    }

    class Message {
        // Display name of the sender
        private String senderName = "";
        // Message contents
        private String message = "";

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
}
