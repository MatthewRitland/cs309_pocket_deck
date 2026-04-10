package com.example.pocketdeck;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
public class WebsocketManager {
    private static WebsocketManager instance;
    private WebSocketClient webSocketClient;
    private WebsocketListener listener;

    private WebsocketManager() {}

    public static synchronized WebsocketManager getInstance() {
        if (instance == null) {
            instance = new WebsocketManager();
        }
        return instance;
    }

    public void setWebSocketListener(WebsocketListener listener) {
        this.listener = listener;
    }

    public void removeWebSocketListener() {
        this.listener = null;
    }

    public void connectWebSocket(String url) {
        try {
            URI uri = URI.create(url);

            webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WS", "Connected");
                    if (listener != null) {
                        listener.onWebSocketOpen(handshakedata);
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WS", "Message: " + message);
                    if (listener != null) {
                        listener.onWebSocketMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WS", "Closed: code=" + code + " reason=" + reason + " remote=" + remote);
                    if (listener != null) {
                        listener.onWebSocketClose(code, reason, remote);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.d("WS", "Error");
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

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
