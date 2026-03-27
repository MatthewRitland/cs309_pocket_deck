package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{

    private Button connectBtn1, connectBtn2, backBtn1, backBtn2;
    private EditText serverEtx1, usernameEtx1, serverEtx2, usernameEtx2, roomEtx1, roomEtx2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initialize UI elements */
        connectBtn1 = (Button) findViewById(R.id.connectBtn);
        connectBtn2 = (Button) findViewById(R.id.connectBtn2);
        backBtn1 = (Button) findViewById(R.id.backBtn);
        backBtn2 = (Button) findViewById(R.id.backBtn2);
        serverEtx1 = (EditText) findViewById(R.id.serverEdt);
        usernameEtx1 = (EditText) findViewById(R.id.unameEdt);
        roomEtx1 = (EditText) findViewById(R.id.roomEdt);
        serverEtx2 = (EditText) findViewById(R.id.serverEdt2);
        usernameEtx2 = (EditText) findViewById(R.id.unameEdt2);
        roomEtx2 = (EditText) findViewById(R.id.roomEdt2);

        /* connect1 button listener */
        connectBtn1.setOnClickListener(view -> {
            String baseUrl = serverEtx1.getText().toString().trim();
            String roomId = roomEtx1.getText().toString().trim();
            String username = usernameEtx1.getText().toString().trim();

            String serverUrl = baseUrl + roomId + "/" + username;

            // start Websocket service with key "chat1"
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "chat1");
            serviceIntent.putExtra("url", serverUrl);
            startService(serviceIntent);

            // got to chat activity #1 based off chat room number
            Intent intent = new Intent(this, ChatActivity1.class);
            intent.putExtra("roomId", roomId);
            startActivity(intent);
        });

        /* connect2 button listener */
        connectBtn2.setOnClickListener(view -> {
            String baseUrl = serverEtx2.getText().toString().trim();
            String roomId = roomEtx2.getText().toString().trim();
            String username = usernameEtx2.getText().toString().trim();
            String serverUrl = baseUrl + roomId + "/" + username;

            // start Websocket service with key "chat2"
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "chat2");
            serviceIntent.putExtra("url", serverUrl);
            startService(serviceIntent);

            // got to chat activity #2
            Intent intent = new Intent(this, ChatActivity2.class);
            startActivity(intent);
        });

        /* back button listener */
        backBtn1.setOnClickListener(view -> {
            // got to chat activity
            Intent intent = new Intent(this, ChatActivity1.class);
            startActivity(intent);
        });

        /* back2 button listener */
        backBtn2.setOnClickListener(view -> {
            // got to chat activity
            Intent intent = new Intent(this, ChatActivity2.class);
            startActivity(intent);
        });
    }
}