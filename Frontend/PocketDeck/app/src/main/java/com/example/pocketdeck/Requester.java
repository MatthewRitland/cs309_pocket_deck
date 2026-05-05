package com.example.pocketdeck;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requester {
    private static final String URL_WS_REQUESTS = "ws://coms-3090-025.class.las.iastate.edu:8080/request/";
    private static final String URL_REQUESTS_RECEIVED = "http://coms-3090-025.class.las.iastate.edu:8080/request/requested/";
    private WebsocketListener listener;
    private static Requester activeRequester;
    private WebSocketClient webSocketClient;
    private Context c;

    List<RequestObject> requestList;

    private Requester(Context c) {
        /* Open websocket */
        UserUtilities userUtils = new UserUtilities(c);
        requestList = new ArrayList<RequestObject>();
        openWebSocket(URL_WS_REQUESTS + userUtils.getSavedUsername());
        this.c = c;
    }

    public static Requester getInstance(Context c) {
        UserUtilities userUtils = new UserUtilities(c);
        if (userUtils.getSavedUsername() == null) return null;

        if (activeRequester == null) {
            activeRequester = new Requester(c);
        }

        activeRequester.FetchLastRequests();
        return activeRequester;
    }

    public void FetchLastRequests() {
        UserUtilities userUtils = new UserUtilities(c);
        long userId = userUtils.getSavedId();
        JsonArrayRequest requestUrl = new JsonArrayRequest(
                Request.Method.GET,
                URL_REQUESTS_RECEIVED+userId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseRequestsArray(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleyCommand.getInstance(c).addToRequestQueue(requestUrl);
    }

    private void parseRequestsArray(JSONArray requestsJson) {
        List<RequestObject> requestsTemp = new ArrayList<RequestObject>();
        try {
            for (int i = 0; i < requestsJson.length(); i++) {
                JSONObject requestJson = requestsJson.getJSONObject(i);
                RequestObject requestObject = new RequestObject(requestJson);
                requestsTemp.add(requestObject);
            }
        } catch (Exception e) {

        }
        requestList = requestsTemp;
        if (listener != null) listener.onWebSocketMessage("UPDATED USERS");
    }

    public void setListener(WebsocketListener newListener) {
        listener = newListener;
    }

    public WebsocketListener removeListener() {
        WebsocketListener oldListener = listener;
        listener = null;
        return oldListener;
    }

    /* Copied from WebsocketManager, not great but whatever works. */
    private void openWebSocket(String path) {
        try {
            URI uri = URI.create(path);

            webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("Requester-WS", "Connected");
                    if (listener != null) {
                        listener.onWebSocketOpen(handshakedata);
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d("Requester-WS", "Message: " + message);
                    FetchLastRequests();

                    if (listener != null) {
                        listener.onWebSocketMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("Requester-WS", "Closed: code=" + code + " reason=" + reason + " remote=" + remote);
                    if (listener != null) {
                        listener.onWebSocketClose(code, reason, remote);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.d("Requester-WS", "Error");
                    if (listener != null) {
                        listener.onWebSocketError(ex);
                    }
                }
            };

            webSocketClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        webSocketClient.close();
    }

    public void sendRequest(String receiverName, long lobbyId) {
        try {
            JSONObject messageObject = new JSONObject();

            messageObject.put("action", "INVITE");
            messageObject.put("targetUsername", receiverName);
            messageObject.put("gameLobbyId", lobbyId);

            Log.d("Requester", messageObject.toString());
            sendMessage(messageObject.toString());
        } catch (Exception e) {
            Log.d("Requester", "Error occurred sending request");
        }
    }

    public void actionRequest(String requesterName, boolean actionAccept) {
        String action = "ACCEPT";
        if (!actionAccept) action = "REJECT";
        try {
            JSONObject messageObject = new JSONObject();
            messageObject.put("action", action);
            messageObject.put("targetUsername", requesterName);
            sendMessage(messageObject.toString());
        } catch (Exception e) {
            Log.d("Requester", "Error occurred acting on request");
        }

    }

    private void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    public List<RequestObject> getRequestList() { return requestList; }
}
