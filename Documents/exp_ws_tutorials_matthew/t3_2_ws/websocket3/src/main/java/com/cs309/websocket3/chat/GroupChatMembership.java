package com.cs309.websocket3.chat;

// should involve the membship status of users. Need to figure out how to
// get websockets into our backend folder without breaking stuff first, though.

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;

//connects Users to a specific group
@Entity
public class GroupChatMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Autowired
    User user;




    // =============================== Getters and Setters for each field ================================== //



}
