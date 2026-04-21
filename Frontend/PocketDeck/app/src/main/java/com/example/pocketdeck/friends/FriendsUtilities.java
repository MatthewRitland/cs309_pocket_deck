package com.example.pocketdeck.friends;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pocketdeck.UserUtilities;
import com.example.pocketdeck.VolleyCommand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsUtilities {
    private static FriendsUtilities friendsUtilities;
    static final String URL_FRIENDS_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/friendships/";
    private List<Friend> friendsList, requestList;
    private ResponseListener currentListener;

    private FriendsUtilities() { }

    public static FriendsUtilities getInstance() {
        if (friendsUtilities == null) {
            friendsUtilities = new FriendsUtilities();
        }

        return friendsUtilities;
    }

    public List<Friend> getFriends() { return friendsList; }

    public List<Friend> getRequests() { return requestList; }

    public void setListener(ResponseListener newListener) {
        if (currentListener != null) { disconnectListener(); }
        currentListener = newListener;
        currentListener.onListenerConnect();
    }

    public void disconnectListener() {
        currentListener.onListenerDisconnect();
        currentListener = null;
    }

    /**
     * Sends a friend request to a user of the specified ID. (Create)
     * @param receiverId User ID of the requested friend.
     */
    public void sendFriendRequest(long requesterId, long receiverId, Context activeContext) {
        String newPath = URL_FRIENDS_PATH + "request/" + requesterId + "/" + receiverId;

        JsonObjectRequest requestSend = new JsonObjectRequest(
                Request.Method.POST,
                newPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO: Handle response object (Friend object)
                        currentListener.onActionSuccess("Friend request sent.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        currentListener.onActionFail(error.getMessage());
                    }
                }
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(requestSend);
    }

    /**
     * Removes the friendship with the specified ID. (Delete)
     * @param friendshipId ID of the friendship connection.
     */
    public void removeFriend(long friendshipId, Context activeContext) {
        String friendshipPath = URL_FRIENDS_PATH + friendshipId;

        JsonObjectRequest requestDelete = new JsonObjectRequest(
                Request.Method.DELETE,
                friendshipPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        fetchRelationships(activeContext);
                        currentListener.onActionSuccess("Friend removed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        currentListener.onActionFail("Issue removing friend");
                    }
                }
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(requestDelete);
    }

    public void confirmRemoval(Friend removingFriend, Context activeContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activeContext);
        builder.setTitle("Are you sure you want to unfriend " + removingFriend.getFriendName() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getInstance().removeFriend(removingFriend.getFriendshipId(), builder.getContext());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Accept a friend request with the ID friendshipId (Update)
     * @param friendshipId ID of the friendship connection.
     */
    public void acceptRequest(long friendshipId, Context activeContext) {
        String newPath = URL_FRIENDS_PATH + "accept/" + friendshipId;

        JsonObjectRequest requestAccept = new JsonObjectRequest(
                Request.Method.PUT,
                newPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        currentListener.onActionSuccess("Friend request accepted");
                        fetchRelationships(activeContext);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Send toast
                        currentListener.onActionFail(error.getMessage());
                    }
                }
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(requestAccept);
    }

    /**
     * Get a list of the users friends in the form of User IDs. (Get)
     */
    public void fetchRelationships(Context activeContext) {
        UserUtilities userUtils = new UserUtilities(activeContext);
        String friendshipPath = URL_FRIENDS_PATH + "received/" + userUtils.getSavedId();


        JsonArrayRequest getFriendsRequest = new JsonArrayRequest(
                Request.Method.GET,
                friendshipPath,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        updateFriendships(response, activeContext);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        VolleyCommand.getInstance(activeContext).addToRequestQueue(getFriendsRequest);
    }

    private void updateFriendships(JSONArray response, Context activeContext) {
        UserUtilities userUtils = new UserUtilities(activeContext);

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
                long relationId = object.getLong("id");
                Log.d("FriendsScreen", Long.toString(relationId));

                // PENDING or FRIEND
                Friend friend = new Friend(friendName, friendId, relationId, requested, friendshipStatus.equals("PENDING"));

                if (friend.getPending()) {
                    //Log.d("FriendsScreen", "REQUEST-" + friend.toString());
                    requestList.add(friend);
                } else {
                    // Friend
                    //Log.d("FriendsScreen", "FRIEND-" + friend.toString());
                    friendsList.add(friend);
                }
            }

            currentListener.onFriendsUpdated();
        } catch (Exception e) {
            currentListener.onActionFail("Failed in updating friends list");
        }
    }

    public interface ResponseListener {
        void onFriendsUpdated();
        void onActionSuccess(String successMessage);
        void onActionFail(String errorMessage);
        void onListenerConnect();
        void onListenerDisconnect();
    }
}
