package com.example.pocketdeck;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsListActivity extends AppCompatActivity {

    /* Page elements */
    private RecyclerView groupView;
    private Button createGroupButton, updateGroupsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_groups);

        /* Get Page Elements */
        groupView = findViewById(R.id.GroupListView);
        createGroupButton = findViewById(R.id.CreateGroupButton);
        // TODO: Add update group button (page motion instead?)

        /* Connect buttons */
    }

    public void getGroups() {
        /* Get the current groups from server (HTTP) */
    }

    private void updateGroups() {
        /* Parse HTTP output into group array */
        /* Update / Setup RecyclerView display */
    }
}
