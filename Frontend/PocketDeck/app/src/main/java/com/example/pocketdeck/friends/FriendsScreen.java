package com.example.pocketdeck.friends;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

/**
 * @author Raine McKellar
 */

public class FriendsScreen extends AppCompatActivity {

    private UserUtilities userUtils;
    static final String URL_FRIENDS_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/friendships/";

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_friends);

        userUtils = new UserUtilities(FriendsScreen.this);

        getRelationships();
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle errors
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
    }

    private void setFriendsView() {

    }

}
