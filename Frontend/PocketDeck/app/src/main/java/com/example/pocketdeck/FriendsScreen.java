package com.example.pocketdeck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * @author Raine McKellar
 */

public class FriendsScreen extends AppCompatActivity implements FriendsUtilities.ResponseListener{

    private UserUtilities userUtils;
    static final String URL_FRIENDS_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/friendships/";
    static final String URL_NAME_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/users/";

    private RecyclerView friendsView, requestView;
    private Button addFriendButton;

    private boolean inReceivedView = false;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_friends);

        userUtils = new UserUtilities(FriendsScreen.this);

        /* Button functionality */
        addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { friendRequestPopup(); }
        });

        friendsView = findViewById(R.id.friendsListView);

        TabLayout tabs = findViewById(R.id.friendsTabBar);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("FriendsScreen", "Tab selected");
                // TODO: REPLACE LATER!!!
                if (tab.getText().toString().equals("Friends")) {
                    inReceivedView = false;
                } else {
                    inReceivedView = true;
                }

                setFriendsView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        FriendsUtilities.getInstance().setListener(this);
        FriendsUtilities.getInstance().fetchRelationships(this);
    }

    public void friendRequestPopup() {
        // Create a friend request popup.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the Friend's User ID (temp)");
        // TODO: USERNAME INSTEAD
        EditText usernameInput = new EditText(this);
        usernameInput.setHint("User ID");
        builder.setView(usernameInput);
        builder.setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idFromNamePath = URL_NAME_PATH + usernameInput.getText().toString().trim();
                // NOTE: There currently isn't any functionality to get the user from a name available.
                // TODO: HTTP REQUEST to find the User ID by their name.
                String input = usernameInput.getText().toString().trim();
                Long id = Long.parseLong(input);
                if (id == null) {
                    Toast.makeText(FriendsScreen.this, "Input is not a valid user ID.", Toast.LENGTH_SHORT).show();
                } else {
                    FriendsUtilities.getInstance().sendFriendRequest(userUtils.getSavedId(), id, FriendsScreen.this);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
        });

        builder.show();
    }


    private void setFriendsView() {
        // Request view
        List<Friend> sendFriends = FriendsUtilities.getInstance().getFriends();
        addFriendButton.setVisibility(View.VISIBLE);
        if (inReceivedView) {
            addFriendButton.setVisibility(View.INVISIBLE);
            sendFriends = FriendsUtilities.getInstance().getRequests();
        }

        FriendsListAdapter adapter = new FriendsListAdapter(sendFriends, inReceivedView, this);
        friendsView.setLayoutManager(new LinearLayoutManager(this));
        friendsView.setAdapter(adapter);
    }

    @Override
    public void onFriendsUpdated() {
        setFriendsView();
    }

    @Override
    public void onActionSuccess(String successMessage) {
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActionFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListenerConnect() { }

    @Override
    public void onListenerDisconnect() { }
}
