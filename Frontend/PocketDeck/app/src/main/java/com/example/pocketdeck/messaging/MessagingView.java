package com.example.pocketdeck.messaging;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdeck.R;
import com.example.pocketdeck.UserUtilities;
import com.example.pocketdeck.WebSocketListener;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagingView extends AppCompatActivity implements WebSocketListener {

    private RecyclerView messageView;
    private MessageViewAdapter messageAdapter;
    private TextView groupNameLabel;
    private EditText messageTextbox;
    private Button messageButton;
    private UserUtilities userUtils;
    // Message Group ID
    private Long messageGroupId;
    private String messageGroupName;

    private static String WebsocketURL = "";

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_messaging);
        messageView = findViewById(R.id.MessagesView);
        groupNameLabel = findViewById(R.id.MessagingGroupName);
        messageTextbox = findViewById(R.id.messageEntryBox);
        messageButton = findViewById(R.id.messageSendButton);
        userUtils = new UserUtilities(this);

        List<Message> messages = new ArrayList<Message>();

        //messages.add(new Message("System","New user joined"));
        messageView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageViewAdapter(messages);
        messageView.setAdapter(messageAdapter);

        /* Unpacking Group */
        Bundle extraData = getIntent().getExtras();
        if (extraData != null && !extraData.isEmpty()) {
            String groupName = extraData.getString("groupName");
            Long groupId = extraData.getLong("groupId");

            // TODO: Websocket connection managing here, likely.
            setGroupInformation(groupId, groupName);
        }

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
                // TODO: Change out for server communication
                addMessage(userMessage);
            }
        });
    }

    public void setGroupInformation(Long groupId, String groupName) {
        this.messageGroupId = groupId;
        this.messageGroupName = groupName;
        groupNameLabel.setText(groupName);
    }

    public void addMessage(Message newMessage) {
        messageAdapter.addNewMessage(newMessage);
        // Scroll to bottom
        messageView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakeData) {
        // TODO : Code
        /* Load previous messages from this group */
    }

    @Override
    public void onWebSocketMessage(String message) {
        // Run on UI
        runOnUiThread(() -> {
            try {
                JSONObject jsonMessage = new JSONObject(message);
                String messageContents = jsonMessage.getString("message");
                String username = jsonMessage.getString("username");
                Message newMessage = new Message(username, messageContents);
            } catch (Exception e) {
                Log.d("MessagingView","Parsing message data failed.");
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) { }
}
