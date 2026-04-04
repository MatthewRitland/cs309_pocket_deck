package com.cs309.websocket3.chat;

import jakarta.persistence.*;

// represents the chatroom itself
@Entity
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String groupName;

    public GroupChat() {
    }

    public GroupChat(String groupName) {
        this.groupName = groupName;
    }



    // =============================== Getters and Setters for each field ================================== //
    public long getId() { return id; }

    String getGroupName() { return groupName; }
    void setGroupName(String groupName) { this.groupName = groupName; }


}
