/**
 * @author Mack Quinn
 */

package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

public class GamePlayScreen extends AppCompatActivity implements WebsocketListener {

    //update later
    //private static final String WS_URL = "ws://coms-3090-025.class.las.iastate.edu:8080/";

    //test for personal ws server
    private static final String WS_URL = "ws://10.0.2.2:8080/game/test1/blackjack";
    private TextView statusText;
    private TextView centerText;
    private Button moveButton1;
    private Button moveButton2;
    private Button moveButton3;
    private Button leaveButton;
    private Button startGameButton;
    private LinearLayout TableCardsL;
    private LinearLayout playerCardsL;
    private WebsocketManager webSocketManager;
    private UserUtilities userUtilities;
    private TextView otherPlayersText;
    private int mySeat = -1;


    private String websocketUrlBuilder() {
        String username = userUtilities.getSavedUsername();
        String selectedGameName = userUtilities.getSelectedGame().toLowerCase();

        if(username == null || username.isEmpty() || "ERR_INVALID_REQUEST".equals(username)){
            username = "guest";
        }

        return "ws://coms-3090-025.class.las.iastate.edu:8080/game/" + username + "/" + selectedGameName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_play_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userUtilities = new UserUtilities(GamePlayScreen.this);
        statusText = findViewById(R.id.statusText);
        centerText = findViewById(R.id.centerText);
        leaveButton = findViewById(R.id.leaveButton);
        moveButton1 = findViewById(R.id.moveButton1);
        moveButton2 = findViewById(R.id.moveButton2);
        moveButton3 = findViewById(R.id.moveButton3);
        startGameButton = findViewById(R.id.startGameButton);
        TableCardsL = findViewById(R.id.TableCardsL);
        playerCardsL = findViewById(R.id.playerCardsLayout);
        otherPlayersText = findViewById(R.id.otherPlayersText);

        statusText.setText("Connecting...");
        centerText.setText("Waiting for game");
        moveButton1.setText("Move 1");
        moveButton2.setText("Move 2");
        moveButton3.setText("Leave");

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GamePlayScreen.this, MainActivity.class);
                startActivity(i);
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("message_type", "start_game");
                    webSocketManager.sendMessage(object.toString());
                    startGameButton.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        moveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMove(moveButton1.getText().toString().toLowerCase());
            }
        });

        moveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMove(moveButton2.getText().toString().toLowerCase());
            }
        });

        moveButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("Leave".contentEquals(moveButton3.getText())) {
                    Intent i = new Intent(GamePlayScreen.this, MainActivity.class);
                    startActivity(i);
                } else {
                    updateMove(moveButton3.getText().toString().toLowerCase());
                }
            }
        });

        webSocketManager = WebsocketManager.getInstance();
        webSocketManager.setWebSocketListener(this);
        //webSocketManager.connectWebSocket(websocketUrlBuilder());
        webSocketManager.connectWebSocket(WS_URL);

    }
    //Websocket connected successfully
    //update UI using runOnUiThread
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText("Connected");
            }
        });

        //Get the selected game mode from pref and make the game message to send to backend

        try {
            String username = userUtilities.getSavedUsername();
            JSONObject object = new JSONObject();
            object.put("message_type", "join_game");
            object.put("username", username);
            webSocketManager.sendMessage(object.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Receive the message from backend
    //update UI using runOnUiThread
    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //statusText.setText(message);
                gameUpdate(message);
            }

        });
    }

    //When websocket closes. needs to be updated to inlclude whole message like the tutorials
    //update UI using runOnUiThread
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //!!update to include the whole reason why
                statusText.setText("Disconnected");
                //statusText.setText(code + reason);
            }
        });
    }

    // Send the message for websocket error
    //update UI using runOnUiThread
    @Override
    public void onWebSocketError(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GamePlayScreen.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Take the json message from backend and determine whats going on
    //should be if statements regarding the message type and the next thing the game does
    //android:background="@drawable/gameplaybackground1"
    private void gameUpdate(String message) {
        try {
            //receive the message
            JSONObject object = new JSONObject(message);
            String messageType = object.optString("message_type", object.optString("messageType", ".."));
            String gamePhase = object.optString("gamePhase", "lobby");

            //so you only see the start button before the game starts
            if ("lobby".equals(gamePhase)) {
                startGameButton.setVisibility(View.VISIBLE);
            } else {
                startGameButton.setVisibility(View.GONE);
            }

            // gamestate update
            if("game_state".equals(messageType)) {
                //update the status text to say if its your turn or someone elses
                statusText.setText(object.optString("status", "Game update"));
                //dealer hand info on the center txt
                centerText.setText(object.optString("centerInfo", "No table info"));

                //set your seat for multiplayer play
                mySeat = object.optInt("yourSeat", -1);
                //get the arary of cards the dealer has or the community cards in go fish or something
                JSONArray centerCards = object.optJSONArray("centerCards");
                int centerCardsHidden = object.optInt("centerHidden", 0);
                //render the cards into the centerCard layout with true meaning theyre face up.
                renderCenterCards(centerCards, centerCardsHidden);

                //get the player cards given
                JSONArray players = object.optJSONArray("players");
                //render the cards for all the players
                renderPlayers(players);

                // get the moves allowed from the backend for the player
                JSONArray moves = object.optJSONArray("actions");
                //update the buttons in the setButtons class
                setButtons(moves);
            } else if("message". equals(messageType)) {
                //this is meant to show if a player is joinging or the game is starting or something
                statusText.setText(object.optString("text", "update"));
            }
        } catch (Exception e) {
            //error stuff
            e.printStackTrace();
            statusText.setText("No game update provided");
        }
    }

    //turn the message text into usable text for the cards
    private String formatCardTextMessage(String value, String suit) {
        String Value;
        String Suit;

        switch (value) {
            case "ACE":
                Value = "A";
                break;
            case "KING":
                Value = "K";
                break;
            case "QUEEN":
                Value = "Q";
                break;
            case "JACK":
                Value = "J";
                break;
            case "TEN":
                Value = "10";
                break;
            case "NINE":
                Value = "9";
                break;
            case "EIGHT":
                Value = "8";
                break;
            case "SEVEN":
                Value = "7";
                break;
            case "SIX":
                Value = "6";
                break;
            case "FIVE":
                Value = "5";
                break;
            case "FOUR":
                Value = "4";
                break;
            case "THREE":
                Value = "3";
                break;
            case "TWO":
                Value = "2";
                break;
            default:
                Value = value;
                break;
        }

        switch (suit) {
            case "SPADES":
                Suit = "♠";
                break;
            case "HEARTS":
                Suit = "♥";
                break;
            case "DIAMONDS":
                Suit = "♦";
                break;
            case "CLUBS":
                Suit = "♣";
                break;
            default:
                Suit = suit;
                break;
        }

        return Value + Suit;
    }

    //renders the dealer cards
    private void renderCenterCards(JSONArray cards, int hiddenCount) {
        //get rid of old cards
        TableCardsL.removeAllViews();

        //render the face up cards first
        if (cards != null) {
            //get cards from json and iterate
            for(int i = 0; i < cards.length(); i++) {
                JSONObject cardObject = cards.optJSONObject(i);

                if(cardObject != null) {
                    String value = cardObject.optString("value", "");
                    String suit = cardObject.optString("suit", "");
                    String text = formatCardTextMessage(value, suit);
                    addCard(TableCardsL, text);
                }
            }
        }
        // render the hidden cards from what backend tells it
        for(int i = 0; i < hiddenCount; i++) {
            addCard(TableCardsL, "");
        }
    }

    //make a card view and add it to the layout
    private void addCard(LinearLayout layout, String text) {
        //inflate the card from the XML
        View cardView = getLayoutInflater().inflate(R.layout.card_holder,layout, false);
        //get the textview inside the card layout
        TextView cardText = cardView.findViewById(R.id.card);

        //. ? and blank all show that the card is hidden so leave these blank to be hidden
        if(".".equals(text) || "??".equals(text) || "".equals(text)) {
            cardText.setText("");
        } else {
            //use the formatter to get the message to proper text and display it
            cardText.setText(text);
        }
        //add the card to the layout
        layout.addView(cardView);
    }


    private void renderPlayers(JSONArray players) {
        //clear out all the previosu cards
        playerCardsL.removeAllViews();

        //check to make sure the backend sent something
        //if nothing sent it will leave
        if (players == null) {
            otherPlayersText.setText("Waiting for players...");
            return;
        }

        //to understand where teh other players are.
        StringBuilder otherPlayers = new StringBuilder();

        //loop through all the players in the game
        for (int i = 0; i < players.length(); i++) {
            JSONObject player = players.optJSONObject(i);

            //make sure the player obj exists
            if (player != null) {
                //get the number of their seat from the backend to assign who is who
                int position = player.optInt("seat", -1);
                //get the players username
                String name = player.optString("username", "Player " + position);
                //get your cards from the array
                JSONArray cards = player.optJSONArray("cards");
                //how many cards the other people have since you cant see them
                int hiddenCount = player.optInt("hiddenCount", 0);

                //if the player is you then render the cards assigned to you
                if (position == mySeat) {
                    renderCards(playerCardsL, cards);
                } else {
                    //if it isnt you then it will get their name and determine the cards to display for them on your screen
                    otherPlayers.append(name).append(" (Seat ").append(position).append(") - ");

                    //if the backend sends visible cards for the other players
                    if (cards != null && cards.length() > 0) {
                        //only shows how many they have
                        otherPlayers.append(cards.length()).append(" cards");
                    } else {
                        //if the backend hides the cards then it will show how many hidden cards
                        otherPlayers.append(hiddenCount).append(" cards");
                    }
                    otherPlayers.append("\n");
                }
            }
        }

        //if only you exist in the lobby it will tell you that
        if (otherPlayers.length() == 0) {
            otherPlayersText.setText("No other players yet");
        } else {
            //build a string of all opponents
            otherPlayersText.setText(otherPlayers.toString().trim());
        }
    }

    //Render the cards with info from the JSON object
    //
    private void renderCards(LinearLayout layout, JSONArray cards) {
        //remove the previous card view when "making" the new cards
        layout.removeAllViews();

        //if the backend doesnt send a cards array then it wont generate anything
        if(cards == null) {
            return;
        }

        //loop through all the cards in the JSON array.
        //if somethings wrong it will return the period
        for(int i = 0; i < cards.length(); i++) {
            JSONObject cardObject = cards.optJSONObject(i);

            if(cardObject != null) {
                String value = cardObject.optString("value", "");
                String suit = cardObject.optString("suit", "");
                String text = formatCardTextMessage(value, suit);
                addCard(layout, text);
            }
        }
    }

    /*
    private void renderPlayerCards(JSONArray players) {
        //clear all the cards in the players hand from the last game or when a new hand starts
        //when the game updates
        playerCardsL.removeAllViews();

        //if the backend doesnt send any players then theres nothing to load
        if(players == null) {
            return;
        }

        //loop through all the players in the array which includes you and friends
        for(int i = 0; i < players.length(); i++) {
            //get the specific player necessary by indexing through the array
            JSONObject player = players.optJSONObject(i);

            //another check to make sure the player exists first
            if(player != null) {
                // find where the player is "sitting" in order for backend to know whos hand is whos.
                // sends -1 if it doesnt exist
                int position = player.optInt("seat", -1);
                //assuming the position 0 is you
                if(position == 0) {
                    //get your cards from the array
                    JSONArray cards = player.optJSONArray("cards");
                    // render the cards onto the screen with the renderCards method.
                    //playerCardsL is the linear layout to keep the cards uniform and in place
                    // cards is the arrau from JSON and the cards shouldnt be hidden so its false
                    renderCards(playerCardsL, cards);
                    return;
                }
            }
        }
    }
     */

    //update the buttons based on the previous action and the game being played
    //all actions that are allowed are based on rules from the backend
    private void setButtons(JSONArray moves) {
        // start with the buttons hidden so they can be changed without the player seeing them change
        moveButton1.setVisibility(View.GONE);
        moveButton2.setVisibility(View.GONE);
        moveButton3.setVisibility(View.GONE);

        // if there are no moves sent from the backend then it will be null or empty
        // show the leaev button so the player has the option to exit

        if (moves == null || moves.length() == 0) {
            moveButton3.setText("Leave");
            moveButton3.setVisibility(View.VISIBLE);
            return;
        }

        // if one possible move exists then asign it to button 1 and make it visible
        if(moves.length() > 0) {
            moveButton1.setText(moves.optString(0, "..."));
            moveButton1.setVisibility(View.VISIBLE);
        }

        // if two moves exist then then assign the second move to the second button and make it visible
        if(moves.length() > 1) {
            moveButton2.setText(moves.optString(1, "..."));
            moveButton2.setVisibility(View.VISIBLE);
        }

        // if 3+ moves exist then assign it to the third button and make it visible
        // if there are are less than 3 moves available it sets the third button as a default leave button for now.
        // will probably change this later
        if(moves.length() > 2) {
            moveButton3.setText(moves.optString(2, "..."));
            moveButton3.setVisibility(View.VISIBLE);
        } else {
            moveButton3.setText("Leave");
            moveButton3.setVisibility(View.VISIBLE);
        }
    }

    //update the move the player made and send it to the backend
    private void updateMove(String moveMade) {
        try {
            JSONObject object = new JSONObject();
            object.put("message_type", "action_made");
            object.put("move", moveMade);

            webSocketManager.sendMessage(object.toString());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.removeWebSocketListener();
        webSocketManager.disconnectWebSocket();
    }
}