package com.cs309.websocket3.chat;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

// doesnt actually store data, right now just is used to recieve incoming messages
// from websocket (the JSON from the websocket)
@Data // @Data uses lambok that makes getters and setters
public class ChatMessageData {
    private String sender;
    private String action;
    private String messageContent;
    //may be useful for thread/forwarding messages? revisit



    /*
    Field 'sender' may have Lombok @Getter -> lambok already included it then?
    public String getSender() {
        return this.sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    */


}
