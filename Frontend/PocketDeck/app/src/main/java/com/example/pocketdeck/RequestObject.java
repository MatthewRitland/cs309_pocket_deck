package com.example.pocketdeck;

import org.json.JSONObject;

public class RequestObject {
    private long requestId;
    private long requesterId;
    private long receiverId;
    private String requestStatus;
    private GameLobby lobbyObject;

    public RequestObject(JSONObject requestJson) {
        try {
            requestId = requestJson.getLong("id");
            requestStatus = requestJson.getString("status");

            JSONObject requesterUser = requestJson.getJSONObject("requester");
            JSONObject receiverUser = requestJson.getJSONObject("requested");
            requesterId = requesterUser.getLong("id");
            receiverId = receiverUser.getLong("id");

            JSONObject lobbyJson = requestJson.getJSONObject("gameLobby");
            lobbyObject = new GameLobby(lobbyJson);

        } catch (Exception e) {
            this.requesterId = 0;
            this.receiverId = 0;
            this.requestStatus = "Err";
        }
    }

    public String getStatus() { return requestStatus; }

    public long getRequestId() { return requestId; }

    public long getRequesterId() { return requesterId; }
    public long getReceiverId() { return receiverId; }

    public GameLobby getLobby() { return lobbyObject; }
}
