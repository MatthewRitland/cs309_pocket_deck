package com.example.pocketdeck;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.foundation.text.StringHelpers_jvmKt;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

public class GameNotes extends AppCompatActivity {
    private EditText noteEdit;
    private Button saveButton;
    private Button deleteButton;
    private Button clearButton;
    private LinearLayout notesListLayout;
    private UserUtilities userUtilities;
    private int userId;
    private int gameId;
    private int currentNoteId = -1;

    private static final String URL = "http://coms-3090-025.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userUtilities = new UserUtilities(this);
        noteEdit = findViewById(R.id.noteEdit);
        saveButton = findViewById(R.id.saveNoteButton);
        deleteButton = findViewById(R.id.deleteNoteButton);
        clearButton = findViewById(R.id.clearNoteButton);
        userId = userUtilities.getSavedId();
        gameId = userUtilities.getSelectedGameId();
        loadNote();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEdit.setText("");
                currentNoteId = -1;
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNoteId != -1) {
                    deleteNote(currentNoteId);
                }
            }
        });
    }

    //get note from backend
    private void loadNote() {
        //create the url for endpoint /gameNotes/{userId}/{gameId}
        String url = URL + "/gameNotes/" + userId + "/" + gameId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //if nothing is returned then clear it
                        if (response.length() == 0) {
                            currentNoteId = -1;
                            noteEdit.setText("");
                            return;
                        }

                        try {
                            // only use one note per game
                            JSONObject note = response.getJSONObject(0);
                            // save id for save/delete
                            currentNoteId = note.getInt("id");
                            //display text in EditText
                            noteEdit.setText(note.getString("text"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        VolleyCommand.getInstance(this).addToRequestQueue(request);
    }

    //figure out to update or create a new note
    private void saveNote() {
        String text = noteEdit.getText().toString();

        // doesnt do anything if empty
        if (text.isEmpty()) {
            return;
        }

        //if no notes exist tehn create one
        if (currentNoteId == -1) {
            createNote(text);
        } else {
            // or update if it exists
            updateNote(currentNoteId, text);
        }
    }

    // this is a post to create a new note
    private void createNote(final String text) {
        // create a url for the endpoint
        String url = URL + "/gameNotes/" + userId + "/" + gameId;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadNote();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {

            // @PostMapping("/gameNotes/{userId}/{gameId}")
            // public GameNote createGameNote(@RequestBody String note, ...)
            // thats the backend mapping for notes so its not expecting an object or JSON it just expects
            // plain text so getBody turns the text into bytes and sends using HTTP since volley wants it to be a byte anywaus
            @Override
            public byte[] getBody() {
                return text.getBytes();
            }

            // this tells the backend that plain text is coming which matches the @REquestBody in the backend
            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        VolleyCommand.getInstance(this).addToRequestQueue(request);
    }

    //this is a put to get the saved note
    private void updateNote(int id, final String text) {
        String url = URL + "/gameNotes/" + id;

        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadNote();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return text.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        VolleyCommand.getInstance(this).addToRequestQueue(request);
    }

    // this sends the delete request
    private void deleteNote(int id) {
        String url = URL + "/gameNotes/" + id;

        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentNoteId = -1;
                        // update ethe UI
                        noteEdit.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        VolleyCommand.getInstance(this).addToRequestQueue(request);
    }
}