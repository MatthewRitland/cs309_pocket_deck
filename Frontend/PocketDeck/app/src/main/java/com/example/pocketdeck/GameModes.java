package com.example.pocketdeck;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class GameModes extends AppCompatActivity {

    // private static final String URL_STRING_REQ = "http://10.0.2.2:3000/login"; // for macoon
    private static final String URL_STRING_REQ = "http://coms-3090-025.class.las.iastate.edu:8080/cardGames"; // for backend
    // private static final String URL_USER_ID = "http://coms-3090-025.class.las.iastate.edu:8080/users/{username}";

    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    // public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    private CardView pokerCard;
    private CardView blackjackCard;
    private CardView futureCard1;
    private CardView futureCard2;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_modes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pokerCard = findViewById(R.id.pokerCard);
        blackjackCard = findViewById(R.id.blackjackCard);
        futureCard1 = findViewById(R.id.futureCard1);
        futureCard2 = findViewById(R.id.futureCard2);
        backButton = findViewById(R.id.backButton);

        // go back to main
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameModes.this, MainActivity.class);
                startActivity(i);

            }
        });

        // poker card
        pokerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameModes.this, MainActivity.class);
                startActivity(i);
            }
        });

        // blackjack card
        blackjackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameModes.this, MainActivity.class);
                startActivity(i);
            }
        });

        // future game card 1
        futureCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // future game card 2
        futureCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.musicPref(this);
    }
}