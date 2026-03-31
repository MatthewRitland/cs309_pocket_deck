package com.example.pocketdeck;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameHistory extends AppCompatActivity {

    // private static final String URL_STRING_REQ = "http://10.0.2.2:3000/login"; // for macoon
    private static final String URL_STRING_REQ = "http://coms-3090-025.class.las.iastate.edu:8080/users/gamehistory/";
    // private static final String URL_USER_ID = "http://coms-3090-025.class.las.iastate.edu:8080/users/{username}";

    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    // public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    private TextView historyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        historyText = findViewById(R.id.historyText);

        UserUtilities userUtils = new UserUtilities(GameHistory.this);
        int userId = userUtils.getSavedId();

        if (userId == -1) {
            historyText.setText("No active user found.");
        } else {
            loadGameHistory(userId);
        }
    }

    private void loadGameHistory(int userId) {

        String newURL = URL_STRING_REQ + userId;
        // create request for a JSON  response
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, newURL,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // build a string to display games
                        StringBuilder stringText = new StringBuilder();

                        // loop through each game
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                int id = obj.optInt("id");
                                String result = obj.optString("gameResult", "N/A");
                                String started = obj.optString("timeGameStarted", "N/A");
                                String completed = obj.optString("timeGameCompleted", "N/A");
                                String duration = obj.optString("timeGameDuration", "N/A");

                                // append formatted game info to string
                                stringText.append("Game #").append(id).append("\n");
                                stringText.append("Result: ").append(result).append("\n");
                                stringText.append("Started: ").append(started).append("\n");
                                stringText.append("Completed: ").append(completed).append("\n");
                                stringText.append("Duration: ").append(duration).append("\n\n");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // if no game history then show message
                        if (stringText.length() == 0) {
                            historyText.setText("No game history found.");
                        } else {
                            //display the text
                            historyText.setText(stringText.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        historyText.setText("Failed to load game history.");
                        //for debugging
                        /*
                        if (error.networkResponse != null) {
                            historyText.setText("Error code: " + error.networkResponse.statusCode);
                        } else {
                            historyText.setText(error.toString());
                        }
                         */
                    }
                }
        );
        VolleyCommand.getInstance(GameHistory.this).addToRequestQueue(request);
    }

        @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.musicPref(this);
    }
}