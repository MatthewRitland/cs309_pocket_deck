package com.example.pocketdeck.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pocketdeck.R;
import com.example.pocketdeck.UserUtilities;
import com.example.pocketdeck.VolleyCommand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupsListActivity extends AppCompatActivity {

    /* HTTP paths */
    static final String URL_GROUP_FETCH = "http://coms-3090-025.class.las.iastate.edu:8080/users/groupChats/";

    /* Page elements */
    private RecyclerView groupView;
    private Button createGroupButton, updateGroupsButton;

    private UserUtilities userUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_groups);

        /* Get Page Elements */
        userUtils = new UserUtilities(this);

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
        JsonArrayRequest groupRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_GROUP_FETCH + userUtils.getSavedId(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        updateGroups(jsonArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // TODO: Handle error
                    }
                }
        );

        VolleyCommand.getInstance(this).addToRequestQueue(groupRequest);
    }

    /**
     * Internal method to update the RecyclerView with the HTTP response.
     * @param response JSON object returned from the HTTP request.
     */
    private void updateGroups(JSONArray response) {
        /* Parse HTTP output into group array */
        List<MessageGroup> groups = parseJsonGroups(response);
        /* Update or Setup RecyclerView display */
        setupRecyclerView(groups);
    }

    private ArrayList<MessageGroup> parseJsonGroups(JSONArray response) {
        ArrayList<MessageGroup> groupList = new ArrayList<MessageGroup>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject newGroupJson = (JSONObject)response.get(i);
                String groupName = newGroupJson.getString("groupName");
                Long groupId = newGroupJson.getLong("id");
                groupList.add(new MessageGroup(groupName, groupId));
            }
        }
        catch (Exception e) {
            /* TODO: Add actual error handling */
        }

        return groupList;
    }

    private void setupRecyclerView(List<MessageGroup> groups) {
        groupView.setLayoutManager(new LinearLayoutManager(this));
        GroupListAdapter groupAdapter = new GroupListAdapter(groups, this);
        groupView.setAdapter(groupAdapter);
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

    private void onGroupCreated(int response) {
        /* Add current user to group */
    }

    /**
     * Open a Websocket to the Groups Messaging and Switch to Messaging Screen.
     * @param groupId Unique groud ID string.
     */
    public void openGroupMessages(Long groupId, String groupName) {
        /* Open Websocket for this group. */


        /* If or when websocket is opened successfully, switch to different view. */
        Intent messageIntent = new Intent(GroupsListActivity.this, MessagingView.class);

        /* Bundle information */
        //Bundle groupBundle = new Bundle();
        //groupBundle.putLong("groupId", groupId);
        //groupBundle.putString("groupName", groupName);
        messageIntent.putExtra("groupId", groupId);
        messageIntent.putExtra("groupName", groupName);
        /* Start activity */
        startActivity(messageIntent);
    }
}
