package com.example.pocketdeck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Raine McKellar
 */

public class FriendsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_friends);
    }

    /**
     * Sends a friend request to a user of the specified ID. (Create)
     * @param requestId User ID of the requested friend.
     */
    private void sendFriendRequest(int requestId) {
        // TODO: CODE
    }

    /**
     * Accept a friend request from a requested of the specified ID. (Update?)
     * @param requesterId User ID of the requester.
     */
    private void acceptRequest(int requesterId) {

    }

    /**
     * Reject a friend request from a requester of the specified ID. (Update?)
     * @param requesterId User ID of the requester.
     */
    private void rejectRequest(int requesterId) {

    }

    /**
     * Removes the friend with the specified ID. (Delete)
     * @param friendId User ID of the friend to be removed.
     */
    private void removeFriend(int friendId) {

    }

    /**
     * Get a list of the users friends in the form of User IDs. (Get)
     * @return Array of friends user ids
     */
    private int[] getFriends() {
        // TODO: CODE
        return new int[]{1};
    }


}
