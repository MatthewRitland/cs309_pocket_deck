package com.example.pocketdeck;

import androidx.annotation.NonNull;

public class FriendObject {
    private final String friendName;
    private final long friendId;
    private final String friendStatus;
    private final boolean requested;
    private final long friendshipId;
    private final boolean pendingFriendship;

    public FriendObject(String name, long id, long friendshipId, boolean requested, boolean pending) {
        this(name, id, friendshipId, "", requested, pending);
    }

    public FriendObject(String name, long id, long friendshipId, String status, boolean requested, boolean pending) {
        this.friendName = name;
        this.friendId = id;
        this.friendshipId = friendshipId;
        this.friendStatus = status;
        this.requested = requested;
        this.pendingFriendship = pending;
    }

    public boolean getRequested() { return requested; }
    public String getFriendName() { return friendName; }
    public long getFriendId() { return friendId; }
    public long getFriendshipId() { return friendshipId; }
    public String getFriendStatus() { return friendStatus; }
    public boolean getPending() { return pendingFriendship; }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + friendName + ", ID: " + friendId;
    }
}
