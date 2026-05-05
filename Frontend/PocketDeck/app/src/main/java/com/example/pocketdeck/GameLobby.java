package com.example.pocketdeck;

import org.json.JSONObject;

public class GameLobby {
    private long lobbyId;
    private int gameMode;
    private boolean inviteOnly;

    public GameLobby(long id, int mode, boolean inviteOnly) {
        this.lobbyId = id;
        this.gameMode = mode;
        this.inviteOnly = inviteOnly;
    }

    public GameLobby(JSONObject lobbyObject) {
        try {
            this.lobbyId = lobbyObject.getLong("id");
            JSONObject gameModeObj = lobbyObject.getJSONObject("cardGame");
            this.gameMode = gameModeObj.getInt("id");
            this.inviteOnly = lobbyObject.getBoolean("isInviteOnly");
        } catch (Exception e) {
            this.lobbyId = 0;
            this.gameMode = 0;
            this.inviteOnly = true;
        }
    }

    public long getLobbyId() { return lobbyId; }
    public int getGameMode() { return gameMode; }
    public boolean isInviteOnly() { return inviteOnly; }
}
