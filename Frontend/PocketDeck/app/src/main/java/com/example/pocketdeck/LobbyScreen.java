package com.example.pocketdeck;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class LobbyScreen extends AppCompatActivity {

    private Button inviteButton, readyButton, leaveButton;
    private RecyclerView activeUserList;

    private boolean isHost, allReadied, localUserReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Standard Initialization */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_lobby);

        /* Get UI elements */
        inviteButton = findViewById(R.id.lobby_inviteButton);
        readyButton = findViewById(R.id.lobby_readyButton);
        leaveButton = findViewById(R.id.lobby_leaveButton);

        /* Initialize UI elements */
        initReadyButton();
    }

    private void allUsersReady() {
        if (!isHost) return;
        allReadied = true;
        readyButton.setText("Begin");
    }

    private void userUnreadied() {
        if(!allReadied) return;
        allReadied = false;

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
        localUserReady = !localUserReady;
        // TODO: Send input out to other users
        updateReadyBtnDisplay();
    }
}
