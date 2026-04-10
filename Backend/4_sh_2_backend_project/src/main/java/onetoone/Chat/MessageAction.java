package onetoone.Chat;
// enumeration for the SPECIFIC actions that a user wants to do when they are sending a message.
// NOT FOR "MESSAGES" ONLY, more what the client wants from the server!!!
public enum MessageAction {
    GET_CHAT_HISTORY,
    SEND,
    CREATE_GROUPCHAT,
    ADD_USER,
    LEAVE,
    DEBUG,
    // more?
}
