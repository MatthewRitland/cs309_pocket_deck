/**
 * @author Mack Quinn
 */

package com.example.pocketdeck;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.pocketdeck.WebsocketListener;

import java.util.Optional;

public class GamePlayScreen extends AppCompatActivity implements WebsocketListener {

    //update later
    private static final String WS_URL = "ws://coms-3090-025.class.las.iastate.edu:8080/";

    private TextView statusText;
    private Button moveButton1;
    private Button moveButton2;
    private Button moveButton3;


    private WebsocketManager webSocketManager;
    private UserUtilities userUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_play_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userUtilities = new UserUtilities(GamePlayScreen.this);
        statusText = findViewById(R.id.statusText);
        moveButton1 = findViewById(R.id.moveButton1);
        moveButton2 = findViewById(R.id.moveButton2);
        moveButton3 = findViewById(R.id.moveButton3);

        statusText.setText("Connecting...");
        moveButton1.setText("Move 1");
        moveButton2.setText("Move 2");
        moveButton3.setText("Leave");

        //!! UPDATE THESE TO SEND GAME INFO
        moveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        moveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        moveButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        webSocketManager = WebsocketManager.getInstance();
        webSocketManager.setWebSocketListener(this);
        webSocketManager.connectWebSocket(WS_URL);
        }
    //Websocket connected successfully
    //update UI using runOnUiThread
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText("Connected");
            }
        });

        //Get the selected game mode from pref and make the game message to send to backend
        try {
            String selectedGameName = userUtilities.getSelectedGame();
            JSONObject object = new JSONObject();
            object.put("messageType", "join_game");
            object.put("gameName", selectedGameName);
            webSocketManager.sendMessage(object.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Receive the message from backend
    //update UI using runOnUiThread
    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameUpdate(message);
            }
        });
    }

    //When websocket closes. needs to be updated to inlclude whole message like the tutorials
    //update UI using runOnUiThread
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //!!update to include the whole reason why
                statusText.setText("Disconnected");
            }
        });
    }

    // Send the message for websocket error
    //update UI using runOnUiThread
    @Override
    public void onWebSocketError(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GamePlayScreen.this, "Websocket error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Take the json message from backend and determine whats going on
    //should be if statements regarding the message type and the next thing the game does
    private void gameUpdate(String message) {
        try {
            JSONObject object = new JSONObject(message);
            String messageType = object.optString("messageType", "..");

            //logic here for game type based on the json name from back end.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update the buttons based on the previous action and the game being played
    //all actions that are allowed are based on rules from the backend
    private void setButtons(JSONArray moves) {

    }

    //update the move the player made and send it to the backend
    private void updateMove(String moveMade) {

    }


}