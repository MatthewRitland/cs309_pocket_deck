package com.example.pocketdeck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MessagingView extends AppCompatActivity {

    private RecyclerView messageView;

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_messaging);
        messageView = findViewById(R.id.MessagesView);

        // Get messages array from server / websocket
        // Update display
    }
}
