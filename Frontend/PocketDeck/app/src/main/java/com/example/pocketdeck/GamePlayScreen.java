/**
 * @author Mack Quinn
 */

package com.example.pocketdeck;

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

public class GamePlayScreen extends AppCompatActivity {

    //private static final String WS_URL = "ws://coms-3090-025.class.las.iastate.edu:8080/";

    private WebsocketManager webSocketManager;

    private TextView statusText;
    private Button moveButton1;
    private Button moveButton2;
    private Button moveButton3;

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

        statusText = findViewById(R.id.statusText);
        moveButton1 = findViewById(R.id.moveButton1);
        moveButton2 = findViewById(R.id.moveButton2);
        moveButton3 = findViewById(R.id.moveButton3);

        statusText.setText("Connecting...");
        moveButton1.setText("Move 1");
        moveButton2.setText("Move 2");
        moveButton3.setText("Leave");

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
        }
}