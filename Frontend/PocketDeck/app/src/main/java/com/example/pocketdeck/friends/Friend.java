package com.example.pocketdeck.friends;

import androidx.annotation.NonNull;

public class Friend {
    private final String friendName;
    private final long friendId;
    private final String friendStatus;
    private final boolean requested;
    private final long friendshipId;

    public Friend(String name, long id, long friendshipId, boolean requested) {
        this(name, id, friendshipId, "", requested);
    }

    public Friend(String name, long id, long friendshipId, String status, boolean requested) {
        this.friendName = name;
        this.friendId = id;
        this.friendshipId = friendshipId;
        this.friendStatus = status;
        this.requested = requested;
    }

    public boolean getRequested() { return requested; }
    public String getFriendName() { return friendName; }
    public long getFriendId() { return friendId; }
    public long getFriendshipId() { return friendshipId; }
    public String getFriendStatus() { return friendStatus; }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + friendName + ", ID: " + friendId;
    }
}
