package com.example.pocketdeck.messaging;

import android.content.Intent;
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
import com.example.pocketdeck.WebsocketListener;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagingView extends AppCompatActivity implements WebsocketListener {

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
            setGroupInformation(groupId, groupName);
        }

        /* Websocket */
        MessagingWebSocketManager.getInstance().setListener(this);
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("action", "GET_CHAT_HISTORY");
            jsonMessage.put("groupChatId", messageGroupId);

            Log.d("Msg-View", jsonMessage.toString());
            MessagingWebSocketManager.getInstance().sendMessage(jsonMessage.toString());
        } catch (Exception e) {
            Log.d("Msg-View", "COULDN'T FETCH INFO");
        }

        Button groupActionButton = findViewById(R.id.groupOptionsButton);
        groupActionButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent optionsIntent = new Intent(MessagingView.this, GroupOptionsActivity.class);
                     optionsIntent.putExtra("groupName", messageGroupName);
                     optionsIntent.putExtra("groupId", messageGroupId);
                     startActivity(optionsIntent);
                 }
             }
        );

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

                messageText = messageText.replace(':',';');

                try {
                    JSONObject newMessage = new JSONObject();
                    newMessage.put("action", "SEND");
                    newMessage.put("messageContent", messageText.trim());
                    newMessage.put("groupChatId", messageGroupId);

                    MessagingWebSocketManager.getInstance().sendMessage(newMessage.toString());
                } catch (Exception e) {
                    Log.d("Msg-View", "Failed to send message");
                }
                //addMessage(userMessage);
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

            String[] singleMessages = message.split("\n");
            for(int i = 0; i < singleMessages.length; i++) {
                if (singleMessages[i].contains("No messages found for")) {
                    continue;
                }
                String[] messageSplit = singleMessages[i].split(": ");

                String username = messageSplit[0];
                String messageContents = "";
                for (int j = 1; j < messageSplit.length; j++) {
                    messageContents += messageSplit[j];
                }

                Message newMessage = new Message(username, messageContents);
                addMessage(newMessage);
            }

        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) { }

    @Override
    public void onWebSocketError(Exception ex) { }
}
