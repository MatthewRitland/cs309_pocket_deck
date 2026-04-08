package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class GroupsListActivity extends AppCompatActivity {

    /* HTTP paths */
    static final String URL_GROUP_FETCH = "";

    /* Page elements */
    private RecyclerView groupView;
    private Button createGroupButton, updateGroupsButton;

    /* Internal Variables */
    private String[] groupNames;
    private String[] groupIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_groups);

        /* Get Page Elements */
        groupView = findViewById(R.id.GroupListView);
        createGroupButton = findViewById(R.id.CreateGroupButton);
        // TODO: Add update group button (page motion instead?)

        /* Connect buttons */

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupPrompt();
            }
        });

        // TODO: UPDATE BUTTON

        /* Initialize */
        getGroups();
    }

    /**
     * Request the Messaging Groups this user communicates with.
     */
    public void getGroups() {
        /* Get the current groups from server (HTTP GET) */
        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GROUP_FETCH + "",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        updateGroups(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // TODO: Handle error
                    }
                }
        );
    }

    /**
     * Internal method to update the RecyclerView with the HTTP response.
     * @param response JSON object returned from the HTTP request.
     */
    private void updateGroups(JSONObject response) {
        /* Parse HTTP output into group array */
        /* Update or Setup RecyclerView display */
    }

    /**
     * Popup a input for a group name, with a confirm and cancel button.
     */
    private void createGroupPrompt() {
        /* Prompt the user for the name of the new group */
    }

    /**
     * Send prompt to the server to create a new group with the designated name.
     * @param groupName Name of the new group.
     */
    public void createNewGroup(String groupName) {
        /* Attempt to create the new group (HTTP PUT) */
    }

    /**
     * Open a Websocket to the Groups Messaging and Switch to Messaging Screen.
     * @param groupId Unique groud ID string.
     */
    public void openGroupMessages(int groupId, String groupName) {
        /* Open Websocket for this group. */


        /* If or when websocket is opened successfully, switch to different view. */
        Intent messageIntent = new Intent(GroupsListActivity.this, MessagingView.class);

        /* Bundle information */
        Bundle groupBundle = new Bundle();
        groupBundle.putInt("groupId", groupId);
        groupBundle.putString("groupName", groupName);

        /* Start activity */
        startActivity(messageIntent, groupBundle);
    }

    public class MessageGroup {
        private String groupName;
        private String groupId;
    }
}
