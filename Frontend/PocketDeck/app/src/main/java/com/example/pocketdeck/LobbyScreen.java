package com.example.pocketdeck;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LobbyScreen extends AppCompatActivity implements WebsocketListener{

    private Button inviteButton, readyButton, leaveButton, publicButton;
    private RecyclerView activeUserList;
    private TextView lobbyLabel;

    static final String URL_SERVER = "http://coms-3090-025.class.las.iastate.edu:8080";

    static final String URL_LOBBY_WEBSOCKET = "ws://coms-3090-025.class.las.iastate.edu:8080/gamelobbies/listenForUpdates/";
    static final String URL_LOBBY_CREATE = URL_SERVER + "/gameLobbies/create/";
    static final String URL_LOBBY_LEAVE = URL_SERVER + "/gameLobbies/leave/";

    private boolean isHost, allReadied, localUserReady;
    private UserUtilities userUtils;
    private long currentLobbyId;
    private GameLobby currentLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Standard Initialization */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_lobby);
        userUtils = new UserUtilities(this);
        Requester.getInstance(this).setListener(this);

        /* Get UI elements */
        inviteButton = findViewById(R.id.lobby_inviteButton);
        readyButton = findViewById(R.id.lobby_readyButton);
        leaveButton = findViewById(R.id.lobby_leaveButton);
        publicButton = findViewById(R.id.lobby_publicToggle);
        lobbyLabel = findViewById(R.id.lobby_gameTitle);
        activeUserList = findViewById(R.id.lobby_joinedUsersList);
        activeUserList.setLayoutManager(new LinearLayoutManager(this));

        if (userUtils.getSelectedGameId() <= 0) {
            userUtils.setSelectedGameID(1);
            userUtils.setSelectedGame("Poker");
        }

        /* Initialize UI elements */
        initReadyButton();
        leaveButton.setOnClickListener(v -> { leaveLobby(); });
        inviteButton.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Username");

            EditText usernameInput = new EditText(this);
            usernameInput.setHint("Username");
            builder.setView(usernameInput);
            builder.setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Requester.getInstance(LobbyScreen.this).sendRequest(usernameInput.getText().toString().trim(), currentLobbyId);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
            });

            builder.show();
        });
        publicButton.setOnClickListener( v -> { changeLobbyVisibility(); });

        /* Get if creating new lobby */
        Bundle extraData = getIntent().getExtras();
        if (extraData == null) {
            // Create a new lobby
            createNewLobby();
        } else {
            // get lobby-id
            publicButton.setVisibility(View.INVISIBLE);
            long lobbyId = extraData.getLong("id");
            connectToLobby(lobbyId);
        }
    }

    private void createNewLobby() {
        isHost = true;
        String createURL = URL_LOBBY_CREATE + userUtils.getSavedId() + "/" + userUtils.getSelectedGameId();
        JsonObjectRequest createRequest = new JsonObjectRequest(
                Request.Method.POST,
                createURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GameLobby lobby = new GameLobby(response);
                        Log.d("LobbyScreen", Long.toString(lobby.getLobbyId()));
                        connectToLobby(lobby.getLobbyId());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LobbyScreen.this, "Failed to create lobby, " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        leaveLobby();
                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(createRequest);
    }

    private void changeLobbyVisibility() {
        if (!isHost) return;
        String lobbyVisPath = URL_SERVER + "/gameLobbies/" + currentLobbyId + "/publicity/" + userUtils.getSavedId();
        JsonObjectRequest changeVisiblityRequest = new JsonObjectRequest(
                Request.Method.PUT,
                lobbyVisPath,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(changeVisiblityRequest);
    }

    private void connectToLobby(long lobbyId) {
        currentLobbyId = lobbyId;
        String webSocketAddress = URL_LOBBY_WEBSOCKET + Long.toString(lobbyId) + "/" + userUtils.getSavedUsername();
        Log.d("LobbyScreen", "WebSocket path = " + webSocketAddress);
        WebsocketManager.getInstance().setWebSocketListener(this);
        WebsocketManager.getInstance().connectWebSocket(webSocketAddress);
    }

    public void leaveLobby() {
        WebsocketManager.getInstance().disconnectWebSocket();
        WebsocketManager.getInstance().removeWebSocketListener();

        String leaveURL = URL_LOBBY_LEAVE + userUtils.getSavedId();
        JsonObjectRequest leaveRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                leaveURL,
                null,
                response -> {
                    Intent newScreen = new Intent(LobbyScreen.this, MainActivity.class);
                    startActivity(newScreen);
                },
                error -> {
                    Toast.makeText(LobbyScreen.this, "Failed to leave", Toast.LENGTH_SHORT).show();
                    Intent newScreen = new Intent(LobbyScreen.this, MainActivity.class);
                    startActivity(newScreen);
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(leaveRequest);
    }

    /* Ready Button */

    private void updateMembers() {
        String membersURL = URL_SERVER + "/gameLobbies/" + currentLobbyId + "/members";
        JsonArrayRequest membersRequest = new JsonArrayRequest(
                Request.Method.GET,
                membersURL,
                null,
                this::parseMembers,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle Error
                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(membersRequest);
    }

    private void parseMembers(JSONArray membersArray) {
        List<LobbyUser> users = new ArrayList<LobbyUser>();
        try {
            boolean allReady = true;
            for (int i = 0; i < membersArray.length(); i++) {
                LobbyUser nextUser = new LobbyUser(membersArray.getJSONObject(i));
                users.add(nextUser);
                if (!nextUser.isReady()) allReady = false;
                JSONObject lobbyObject = membersArray.getJSONObject(i).getJSONObject("gameLobby");
                currentLobby = new GameLobby(lobbyObject);
                Log.d("LobbyScreen", nextUser.toString());
            }
            if (allReady) allUsersReady();
            else userUnreadied();
            activeUserList.setAdapter(new LobbyListAdapter(users));
            String gameName = "None";
            switch (currentLobby.getGameMode()) {
                case 1:
                    gameName = "Poker";
                    break;
                case 2:
                    gameName = "Blackjack";
                    break;
                case 3:
                    gameName = "Garbage";
                    break;
                case 4:
                    gameName = "Solitare";
                    break;
            }
            lobbyLabel.setText(gameName);
            publicButton.setText(currentLobby.isInviteOnly()? "Make Private":"Make Public");

        } catch (Exception e) {
            // TODO: Handle
        }
    }

    private void allUsersReady() {
        if (!isHost) return;
        allReadied = true;
        readyButton.setText("Begin");
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStartGame();
            }
        });
    }

    private void userUnreadied() {
        if(!allReadied) return;
        allReadied = false;
        initReadyButton();
    }

    private void initReadyButton() {
        updateReadyBtnDisplay();
        readyButton.setOnClickListener(v -> onReadyClicked());
    }

    private void updateReadyBtnDisplay() {
        if (localUserReady) {
            readyButton.setText("Unready");
        }
        else {
            readyButton.setText("Ready");
        }
    }

    private void onReadyClicked() {
        readyButton.setEnabled(false);

        String readyUrl = URL_SERVER + "/gameLobbies/ready/" + userUtils.getSavedId();
        JsonObjectRequest readyRequest = new JsonObjectRequest(
                Request.Method.PUT,
                readyUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            localUserReady = response.getBoolean("isReady");
                        } catch (Exception e) {}
                        updateReadyBtnDisplay();
                        readyButton.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        readyButton.setEnabled(true);
                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(readyRequest);
    }

    public void requestStartGame() {
        if (!isHost) return;
        String beginGameUrl = URL_SERVER + "/gameLobbies/" + currentLobbyId + "/start/" + userUtils.getSavedId();
        JsonObjectRequest beginRequest = new JsonObjectRequest(
                Request.Method.POST,
                beginGameUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LobbyScreen.this, "Couldn't start game", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleyCommand.getInstance(this).addToRequestQueue(beginRequest);
    }

    private void startGame() {
        /* AREA FOR CONNECTING CODE!!! */
        Intent i = new Intent(LobbyScreen.this, GamePlayScreen.class);
        startActivity(i);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        updateMembers();
    }

    @Override
    public void onWebSocketMessage(String message) {
        /* Send HTTP get request for users and ready status */
        try {
            JSONObject messageJson = new JSONObject(message);
            String messageType = messageJson.getString("type");
            if (messageType.equals("LOBBY_UPDATE")) {
                updateMembers();
            } else if (messageType.equals("GAME_START")){
                startGame();
            }
        } catch (Exception e) {
            
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}

    @Override
    public void onWebSocketError(Exception ex) { }

    class LobbyUser {
        private String username;
        private long userId;
        private boolean ready;
        private boolean host;

        public LobbyUser(JSONObject userObject) {
            try {
                this.ready = userObject.getBoolean("isReady");
                this.host = userObject.getString("memberRole").equals("OWNER_MEMBER");
                JSONObject userInfo = userObject.getJSONObject("gameLobbyMember");
                this.username = userInfo.getString("username");
                this.userId = userInfo.getLong("id");
            } catch (Exception e) {
                this.ready = false;
                this.host = false;
                this.username = "Err";
                this.userId = 0;
            }
        }

        public String getUsername() { return username; }
        public long getUserId() { return userId; }
        public boolean isReady() { return ready; }
        public boolean isHost() { return host; }

        @NonNull
        @Override
        public String toString() {
            String output = username + " ";
            if (isHost()) output += "- Host ";
            output += "| ";
            if (isReady()) {
                output += "READY";
            } else {
                output += "NOT READY";
            }

            return output;
        }
    }
}
