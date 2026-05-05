package PocketDeck.Requests;

public class RequestMessageData {
    private RequestMessageAction action;
    private String targetUsername;
    private int gameLobbyId; // may use Integer so it can be null, then change this field to nullable in Request.java

    public RequestMessageData() {}





    // =============================== Getters and Setters for each field ================================== //
    public RequestMessageAction getAction() { return this.action; }
    public void setAction(RequestMessageAction action) { this.action = action; }

    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

    // Integer, NOT int so it may be null! (if request was not being used for only gameLobby)
    public int getGameLobbyId() { return gameLobbyId; }
    public void setGameLobbyId(Integer gameLobbyId) {
        this.gameLobbyId = gameLobbyId;
    }
}
