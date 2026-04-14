package com.example.pocketdeck.friends;

public class Friend {
    private final String friendName;
    private final int friendId;
    private final String friendStatus;

    public Friend(String name, int id) {
        this(name, id, "");
    }

    public Friend(String name, int id, String status) {
        this.friendName = name;
        this.friendId = id;
        this.friendStatus = status;
    }

    public String getFriendName() { return friendName; }
    public int getFriendId() { return friendId; }
    public String getFriendStatus() { return friendStatus; }
}
