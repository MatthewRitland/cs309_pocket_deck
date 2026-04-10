package com.example.pocketdeck.messaging;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketdeck.R;

import org.json.JSONObject;

public class GroupOptionsActivity extends AppCompatActivity {


    private long groupId;
    private String groupName;

    private EditText usernameInput;
    private Button leaveGroup, addUser;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

        setContentView(R.layout.activity_group_options);

        leaveGroup = findViewById(R.id.leaveGroupButton);
        addUser = findViewById(R.id.groupInviteButton);
        usernameInput = findViewById(R.id.groupInviteName);

        Bundle extraBundle = getIntent().getExtras();
        groupName = extraBundle.getString("groupName");
        groupId = extraBundle.getLong("groupId");

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToGroup(usernameInput.getText().toString().trim());
            }
        });

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });


    }

    private void addUserToGroup(String username) {
        try {
            JSONObject messageObject = new JSONObject();
            messageObject.put("action", "ADD_USER");
            messageObject.put("messageContent", username);
            messageObject.put("groupChatId", groupId);

            MessagingWebSocketManager.getInstance().sendMessage(messageObject.toString());
        } catch (Exception e) {
            Log.d("GroupOptions", "Failed to add user");
        }
    }

    private void leaveGroup() {
        try {
            JSONObject messageObject = new JSONObject();
            messageObject.put("action", "LEAVE");
            messageObject.put("groupChatId", groupId);

            MessagingWebSocketManager.getInstance().sendMessage(messageObject.toString());
        } catch (Exception e) {
            Log.d("GroupOptions", "Failed to leave");
        }
    }
}
