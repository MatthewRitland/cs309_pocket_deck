/**
 * @author Mack Quinn
 */

package com.example.pocketdeck;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Button playButton;
    private Button modeButton;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.expandingMenu); // this is the expanding menu top left
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MusicPlayer.startMusic(this);

        modeButton = findViewById(R.id.modeButton);

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameModes.class);
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
        playButton = findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //open the file named userLoggedInCheck to check login status from LoginActivity
                SharedPreferences preferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
                //isLoggedIn is stored in the file and if it gdoesnt exist set it to false.
                boolean loggedIn = preferences.getBoolean("isLoggedIn", false);


                /*
                Intent i = new Intent(MainActivity.this, GamePlayScreen.class);
                startActivity(i);


                 */

                // if logged in go to the gameplay screen if not then
                if(loggedIn) {
                    //Intent i = new Intent(MainActivity.this, GamePlayScreen.class);
                    Intent i = new Intent(MainActivity.this, LobbyScreen.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });

    }
    // This will access the XML to put the menu top right

    /**
     *
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // This is a menu method that handles menu clicks

    /**
     *
     * @param item The menu item that was selected.
     *
     * @return
     */
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

        if (id == R.id.nav_messaging) {
            Intent i = new Intent(MainActivity.this, GroupsListActivity.class);
            startActivity(i);
        }

        if (id == R.id.nav_notes) {
            Intent i = new Intent(MainActivity.this, GameNotes.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.musicPref(this);
    }
}