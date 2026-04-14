package com.example.pocketdeck.friends;

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

/**
 * @author Raine McKellar
 */

public class FriendsScreen extends AppCompatActivity {

    private UserUtilities userUtils;
    static final String URL_FRIENDS_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/friendships/";
    static final String URL_NAME_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/users/";

    RecyclerView friendsView, requestView;

    List<Friend> friendsList, requestList;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_friends);

        userUtils = new UserUtilities(FriendsScreen.this);

        /* Button functionality */
        Button addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { friendRequestPopup(); }
        });

        friendsView = findViewById(R.id.friendsListView);

        getRelationships();
    }

    public void friendRequestPopup() {
        // Create a friend request popup.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the Friend's username");

        EditText usernameInput = new EditText(this);
        usernameInput.setHint("Username");
        builder.setView(usernameInput);
        builder.setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idFromNamePath = URL_NAME_PATH + usernameInput.getText().toString().trim();
                // NOTE: There currently isn't any functionality to get the user from a name available.
                // TODO: HTTP REQUEST for User by their name.
                Toast.makeText(FriendsScreen.this, "DEMO: No backend yet", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
        });

        builder.show();
    }

    /**
     * Sends a friend request to a user of the specified ID. (Create)
     * @param requestId User ID of the requested friend.
     */
    private void sendFriendRequest(int requestId) {
        String newPath = URL_FRIENDS_PATH + "request/" + userUtils.getSavedId() + "/" + requestId;

        JsonObjectRequest requestSend = new JsonObjectRequest(
                Request.Method.POST,
                newPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO: Handle response object (Friend object)
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle errors.
                    }
                }
        );

        VolleyCommand.getInstance(FriendsScreen.this).addToRequestQueue(requestSend);
    }

    /**
     * Accept a friend request with the ID friendshipId (Update)
     * @param friendshipId ID of the friendship connection.
     */
    private void acceptRequest(int friendshipId) {
        String newPath = URL_FRIENDS_PATH + "accept/" + friendshipId;

        JsonObjectRequest requestAccept = new JsonObjectRequest(
                Request.Method.PUT,
                newPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO: Handle response
                        getRelationships();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Send toast
                    }
                }
        );

        VolleyCommand.getInstance(FriendsScreen.this).addToRequestQueue(requestAccept);
    }

    /**
     * Removes the friendship with the specified ID. (Delete)
     * @param friendshipId ID of the friendship connection.
     */
    private void removeFriend(int friendshipId) {
        String friendshipPath = URL_FRIENDS_PATH + friendshipId;

        JsonObjectRequest requestDelete = new JsonObjectRequest(
                Request.Method.DELETE,
                friendshipPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO : Success response (toast)


                        // Update relationships
                        getRelationships();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO : Handle error
                    }
                }
        );

        VolleyCommand.getInstance(FriendsScreen.this).addToRequestQueue(requestDelete);
    }

    /**
     * Get a list of the users friends in the form of User IDs. (Get)
     */
    private void getRelationships() {
        String friendshipPath = URL_FRIENDS_PATH + "received/" + userUtils.getSavedId();

        JsonArrayRequest getFriendsRequest = new JsonArrayRequest(
                Request.Method.GET,
                friendshipPath,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // TODO: Handle responses
                        updateFriendships(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle Errors
                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(getFriendsRequest);
    }

    private void updateFriendships(JSONArray response) {
        // TODO : Parse response
        friendsList = new ArrayList<Friend>();
        requestList = new ArrayList<Friend>();

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = (JSONObject)response.get(i);
                String friendshipStatus = object.getString("friendshipStatus");
                JSONObject requester = object.getJSONObject("requester");
                JSONObject receiver = object.getJSONObject("receiver");

                JSONObject friendObject;
                boolean requested = requester.getLong("id") == userUtils.getSavedId();

                if (requested) friendObject = receiver;
                else friendObject = requester;

                String friendName = friendObject.getString("username");
                long friendId = friendObject.getLong("id");

                // PENDING or FRIEND
                Friend friend = new Friend(friendName, friendId, requested);

                if (friendshipStatus.equals("PENDING")) {
                    Log.d("FriendsScreen", "REQUEST-" + friend.toString());
                    requestList.add(friend);
                } else {
                    // Friend
                    Log.d("FriendsScreen", "FRIEND-" + friend.toString());
                    friendsList.add(friend);
                }
            }
            setFriendsView();
        } catch (Exception e) {
            // TODO : Error Handling
        }
    }

    private void setFriendsView() {
        // Request view
        FriendsListAdapter adapter = new FriendsListAdapter(friendsList, false);
        friendsView.setLayoutManager(new LinearLayoutManager(this));
        friendsView.setAdapter(adapter);
    }

}
