package com.example.pocketdeck;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagingView extends AppCompatActivity{

    private RecyclerView messageView;
    private MessageViewAdapter messageAdapter;
    private EditText messageTextbox;
    private Button messageButton;
    private UserUtilities userUtils;

    private static String WebsocketURL = "";

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_messaging);
        messageView = findViewById(R.id.MessagesView);
        messageTextbox = findViewById(R.id.messageEntryBox);
        messageButton = findViewById(R.id.messageSendButton);
        userUtils = new UserUtilities(this);

        List<Message> messages = new ArrayList<Message>();
        // Demo lines
        //messages.add(new Message("System","New user joined"));
        messageView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageViewAdapter(messages);
        messageView.setAdapter(messageAdapter);
        /* Websocket Connection */
        

        // Adding messages upon entering
        messageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Get message text
                String messageText = messageTextbox.getText().toString();
                if (messageText.length() > 250) {
                    messageText = messageText.substring(0,250);
                }
                // Clear text
                messageTextbox.setText("");
                // Get username
                String username = userUtils.getSavedUsername();

                Message userMessage = new Message(username, messageText);
                addMessage(userMessage);

            }
        });
    }

    public void addMessage(Message newMessage) {
        messageAdapter.addNewMessage(newMessage);
        // Scroll to bottom
        messageView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
}
