package com.example.pocketdeck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagingView extends AppCompatActivity {

    private RecyclerView messageView;

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_messaging);
        messageView = findViewById(R.id.MessagesView);

        List<Message> messages = new ArrayList<Message>();
        // Demo lines
        messages.add(new Message("Demo User","I am speaking presently"));
        messages.add(new Message("Non-user", "How did I get here?"));
        messages.add(new Message("I AM THE YAPPER", "HERPADERPADOOBEEDOODOOHAHA I call" +
                " upon the great powers beyond the veil to cast lightning storm on your pathetic being. It" +
                " would really be funny, I think. I've written in this single textbox more than I ever have" +
                " before in my dumb life. Are you proud of me, father?"));

        messageView.setLayoutManager(new LinearLayoutManager(this));

        MessageViewAdapter messageAdapter = new MessageViewAdapter(messages);
        messageView.setAdapter(messageAdapter);
        // Get messages array from server / websocket
        // Update display
    }


}
