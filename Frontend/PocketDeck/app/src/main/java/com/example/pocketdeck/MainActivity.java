package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button gameHistoryButton;
    private Button accountButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_menu);

        Toolbar toolbar = findViewById(R.id.expandingMenu); // this is the expanding menu top left
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gameHistoryButton = findViewById(R.id.gameHistoryButton);


        gameHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        /*

        gameHistoryButton = findViewById(R.id.gameHistoryButton);
        accountButton = findViewById(R.id.accountButton);
        settingsButton = findViewById(R.id.settingsButton);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AccountSettings.class);
                startActivity(i);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });
        */
    }

    // This will access the XML to put the menu top right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // This is a menu method that handles menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            Intent i = new Intent(MainActivity.this, GameHistory.class);
            startActivity(i);
        }

        if (id == R.id.nav_account) {
            Intent i = new Intent(MainActivity.this, AccountSettings.class);
            startActivity(i);
        }

        if (id == R.id.nav_settings) {
            Intent i = new Intent(MainActivity.this, Settings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}