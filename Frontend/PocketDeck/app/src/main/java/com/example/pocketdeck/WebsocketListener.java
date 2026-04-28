package com.example.pocketdeck;

import org.java_websocket.handshake.ServerHandshake;

    public interface WebsocketListener {
        void onWebSocketOpen(ServerHandshake handshakedata);

        void onWebSocketMessage(String message);

        void onWebSocketClose(int code, String reason, boolean remote);

        void onWebSocketError(Exception ex);
    }
