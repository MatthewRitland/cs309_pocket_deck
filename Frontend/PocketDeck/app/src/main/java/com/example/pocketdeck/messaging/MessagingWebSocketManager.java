package com.example.pocketdeck.messaging;

import android.util.Log;

import com.example.pocketdeck.WebSocketListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MessagingWebSocketManager {

    private static MessagingWebSocketManager instance;
    private MessagingClient webSocketClient;
    private WebSocketListener currentListener;

    /* Singleton instancing */

    private MessagingWebSocketManager() {}

    public static MessagingWebSocketManager getInstance() {
        if (instance == null) {
            instance = new MessagingWebSocketManager();
        }
        return instance;
    }

    /**
     * Connect a WebSocketListener to this WebSocket.
     * There can only be one listener attached to a socket at a time.
     *
     * @param newListener Listener to be assigned
     */
    public void setListener(WebSocketListener newListener) {
        this.currentListener = newListener;
    }

    /**
     * Clear listener
     */
    public void clearListener() {
        this.currentListener = null;
    }

    public void connectWebSocket(String serverUrl) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            Log.d("WbSktManager", "Websocket already open");
            return;
        }

        try {
            URI serverUri = URI.create(serverUrl);
            webSocketClient = new MessagingClient(serverUri);
            webSocketClient.connect();
        } catch (Exception e) {
            /* TODO: Clean exception handling */
            Log.d("Msg-WbSktManager","Error occurred when connecting WebSocket");
            //e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        Log.d("WbSktManager", message);
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    /* Client */

    private class MessagingClient extends WebSocketClient {
        public MessagingClient (URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            // On opening server
            Log.d("WbSktManager", handshakedata.toString());
            if (currentListener != null) currentListener.onWebSocketOpen(handshakedata);
        }

        @Override
        public void onMessage(String message) {
            // On receiving a message
            Log.d("WbSktManager", message);
            if (currentListener != null) currentListener.onWebSocketMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            // On closing the connection
            if (currentListener != null) currentListener.onWebSocketClose(code, reason, remote);
        }

        @Override
        public void onError(Exception ex) {
            // When an error occurs.
            Log.d("WbSktManager", ex.getMessage());
            if (currentListener != null) currentListener.onWebSocketError(ex);
        }
    }
}
