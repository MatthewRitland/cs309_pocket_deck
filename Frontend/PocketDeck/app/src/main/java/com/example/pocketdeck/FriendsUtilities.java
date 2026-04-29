package com.example.pocketdeck;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsUtilities {
    private static FriendsUtilities friendsUtilities;
    static final String URL_FRIENDS_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/friendships/";
    static final String URL_USER_USERNAME = "http://coms-3090-025.class.las.iastate.edu:8080/users/username/";
    private List<FriendObject> friendsList, requestList;
    private ResponseListener currentListener;

    private FriendsUtilities() { }

    public static FriendsUtilities getInstance() {
        if (friendsUtilities == null) {
            friendsUtilities = new FriendsUtilities();
        }

        return friendsUtilities;
    }

    public List<FriendObject> getFriends() { return friendsList; }

    public List<FriendObject> getRequests() { return requestList; }

    public void setListener(ResponseListener newListener) {
        if (currentListener != null) { disconnectListener(); }
        currentListener = newListener;
        currentListener.onListenerConnect();
    }

    public void disconnectListener() {
        currentListener.onListenerDisconnect();
        currentListener = null;
    }

    public void fetchFriendRequests(long receiverId, Context activeContext) {
        String receivedPath = URL_FRIENDS_PATH + "requests/received/" + receiverId;
        String requestedPath = URL_FRIENDS_PATH + "requests/sent/" + receiverId;

        requestList = new ArrayList<>();

        JsonArrayRequest sentRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestedPath,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        updateRequested(jsonArray, activeContext, false);
                    }
                },
                volleyError -> currentListener.onActionFail(volleyError.getMessage())
        );

        JsonArrayRequest receivedRequest = new JsonArrayRequest(
                Request.Method.GET,
                receivedPath,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        updateRequested(jsonArray, activeContext, true);
                    }
                },
                volleyError -> currentListener.onActionFail(volleyError.getMessage())
        );
        VolleyCommand.getInstance(activeContext).addToRequestQueue(sentRequest);
        VolleyCommand.getInstance(activeContext).addToRequestQueue(receivedRequest);
    }

    public void fetchAcceptedFriends(long receiverId, Context activeContext){
        String requestPath = URL_FRIENDS_PATH + "accepted/" + receiverId;
        requestList = new ArrayList<FriendObject>();

        JsonArrayRequest volleyRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestPath,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        updateAccepted(jsonArray, activeContext);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        currentListener.onActionFail(volleyError.getMessage());
                    }
                }
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(volleyRequest);
    }

    public void sendFriendRequest(long requesterId, String receiverUsername, Context activeContext) {
        String userByNamePath = URL_USER_USERNAME + receiverUsername;
        JsonObjectRequest userRequest = new JsonObjectRequest(
                Request.Method.GET,
                userByNamePath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            long userId = response.getLong("id");
                            sendFriendRequest(requesterId, userId, activeContext);

                        } catch (Exception e) {
                            currentListener.onActionFail(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        currentListener.onActionFail(error.getMessage());
                    }
                }
        );
        VolleyCommand.getInstance(activeContext).addToRequestQueue(userRequest);
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
                        currentListener.onActionSuccess("Friend request sent.");
                    }
                },
                error -> currentListener.onActionFail(error.getMessage())
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
                error -> currentListener.onActionFail("Issue removing friend")
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(requestDelete);
    }

    public void confirmRemoval(FriendObject removingFriend, Context activeContext) {
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
                error -> currentListener.onActionFail(error.getMessage())
        );

        VolleyCommand.getInstance(activeContext).addToRequestQueue(requestAccept);
    }

    /**
     * Get a list of the users friends in the form of User IDs. (Get)
     */
    public void fetchRelationships(Context activeContext) {
        UserUtilities userUtils = new UserUtilities(activeContext);

        friendsList = new ArrayList<FriendObject>();
        requestList = new ArrayList<FriendObject>();

        fetchFriendRequests(userUtils.getSavedId(), activeContext);
        fetchAcceptedFriends(userUtils.getSavedId(), activeContext);
    }

    private void updateAccepted(JSONArray response, Context activeContext) {
        UserUtilities userUtils = new UserUtilities(activeContext);

        friendsList = new ArrayList<FriendObject>();

        try {
            List<FriendObject> friends = parseFriendsList(response, userUtils.getSavedId());
            friendsList.addAll(friends);
            currentListener.onFriendsUpdated();
        } catch (Exception e) {
            currentListener.onActionFail("Failed in updating friends list");
        }
    }

    private void updateRequested(JSONArray response, Context activeContext, boolean appendBefore) {
        UserUtilities userUtils = new UserUtilities(activeContext);

        //friendsList = new ArrayList<FriendObject>();

        try {
            List<FriendObject> friends = parseFriendsList(response, userUtils.getSavedId());
            if (appendBefore) {
                requestList.addAll(0,friends);
            } else {
                requestList.addAll(friends);
            }
            currentListener.onFriendsUpdated();
        } catch (Exception e) {
            currentListener.onActionFail("Failed in updating friends list");
        }
    }

    private List<FriendObject> parseFriendsList(JSONArray friendshipList, long userId) throws JSONException{
        List<FriendObject> friends = new ArrayList<FriendObject>();
        for (int i = 0; i < friendshipList.length(); i++) {
            JSONObject friendship = friendshipList.getJSONObject(i);
            FriendObject friend = parseFriendFromJson(friendship, userId);
            friends.add(friend);
        }
        return friends;
    }

    private FriendObject parseFriendFromJson(JSONObject friendship, long userId) throws JSONException {
        String friendshipStatus = friendship.getString("friendshipStatus");
        JSONObject requester = friendship.getJSONObject("requester");
        JSONObject receiver = friendship.getJSONObject("receiver");

        JSONObject friendObject;
        boolean requested = requester.getLong("id") == userId;

        if (requested) friendObject = receiver;
        else friendObject = requester;

        String friendName = friendObject.getString("username");
        long friendId = friendObject.getLong("id");
        long relationId = friendship.getLong("id");

        return new FriendObject(friendName, friendId, relationId, requested, friendshipStatus.equals("PENDING"));
    }

    public interface ResponseListener {
        void onFriendsUpdated();
        void onActionSuccess(String successMessage);
        void onActionFail(String errorMessage);
        void onListenerConnect();
        void onListenerDisconnect();
    }
}
