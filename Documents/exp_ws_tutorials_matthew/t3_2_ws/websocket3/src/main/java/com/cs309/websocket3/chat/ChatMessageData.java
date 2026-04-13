package com.cs309.websocket3.chat;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

// doesnt actually store data, right now just is used to recieve incoming messages
// from websocket (the JSON from the websocket)
@Data // @Data uses lambok that makes getters and setters
public class ChatMessageData {
    private String sender; //who sent this message content?

    private MessageAction action; // will be SEND, LEAVE... is the action the user wants inside of a @OnMessage
    private String messageContent;
    private Long groupId; //what group does this message content belong to?
    //may be useful for thread/forwarding messages? revisit


    // =============================== Getters and Setters for each field ================================== //
    /*
    Field 'sender' may have Lombok @Getter -> lambok already included    it then?
    public String getSender() {
        return this.sender;
    }
    */


}
