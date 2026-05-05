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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class GameHistory extends AppCompatActivity {

    // private static final String URL_STRING_REQ = "http://10.0.2.2:3000/login"; // for macoon
    private static final String URL_STRING_REQ = "http://coms-3090-025.class.las.iastate.edu:8080/users/gamehistory/";
    // private static final String URL_USER_ID = "http://coms-3090-025.class.las.iastate.edu:8080/users/{username}";

    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    // public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    private RecyclerView historyRecycler;
    private GameHistoryHelper helper;
    private ArrayList<String> historyList;

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

        historyRecycler = findViewById(R.id.historyRecyclerView);
        historyRecycler.setLayoutManager(new LinearLayoutManager(GameHistory.this));
        historyList = new ArrayList<String>();
        helper = new GameHistoryHelper(historyList);
        historyRecycler.setAdapter(helper);
        UserUtilities userUtils = new UserUtilities(GameHistory.this);

        int userId = userUtils.getSavedId();
        
        if (userId == -1) {
            historyList.add("No active user found.");
        }
        else {
            loadGameHistory(userId);
        }
    }

    private void loadGameHistory(int userId) {
        String newURL = URL_STRING_REQ + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, newURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // Clear old stuff first so rows dont build up
                        historyList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                // the JSON from the array
                                JSONObject obj = response.getJSONObject(i);

                                // values from the backend
                                int id = obj.optInt("id");
                                String result = obj.optString("gameResult", "N/A");
                                String started = obj.optString("timeGameStarted", "N/A");
                                String completed = obj.optString("timeGameCompleted", "N/A");
                                String duration = obj.optString("timeGameDuration", "N/A");

                                // get the name
                                JSONObject cardGameObject = obj.optJSONObject("cardGame");

                                // in case cardGame is missing
                                String gameName = "No Name";

                                // If the object exists get the gameName
                                if (cardGameObject != null) {
                                    gameName = cardGameObject.optString("gameName", "invalid");
                                }

                                // Build one string to display
                                String gameInfo = "Game name: " + gameName + "\n" + "Game #" + id + "\n" + "Result: " + result + "\n" + "Started: " + started + "\n" + "Completed: " + completed + "\n" + "Duration: " + duration;

                                // Add this game to the list
                                historyList.add(gameInfo);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // If backend returned an empty array show a message
                        if (historyList.size() == 0) {
                            historyList.add("No game history found.");
                        }

                        // tell it to get the info again
                        helper.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        // clear  old data
                        historyList.clear();
                        historyList.add("Failed to load game history.");
                        // Refresh recylcer
                        helper.notifyDataSetChanged();
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