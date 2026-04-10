package com.example.pocketdeck.messaging;

public class MessageGroup {
    private String groupName;
    private Long groupId;

    public MessageGroup(String groupName, Long groupId) {
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public String getGroupName() { return groupName; }
    public Long getGroupId() { return groupId; }
}
