package com.cs309.websocket3.chat;
// enumeration for the SPECIFIC actions that a user wants to do when they are sending a message.
// NOT FOR "MESSAGES" ONLY, more what the client wants from the server!!!
public enum MessageAction {
    SEND,
    ADD_USER,
    LEAVE
    // more?
}
