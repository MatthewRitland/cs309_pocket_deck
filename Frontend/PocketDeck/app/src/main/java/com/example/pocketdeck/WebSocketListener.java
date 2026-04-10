package com.example.pocketdeck;

import org.java_websocket.handshake.ServerHandshake;

public interface WebSocketListener {

    /**
     * Called on successful connection to the websocket.
     *
     * @param handshakeData Information on the Servers Handshake
     */
    void onWebSocketOpen(ServerHandshake handshakeData);

    /**
     * Called when a WebSocket message is received.
     *
     * @param message The received WebSocket message.
     */
    void onWebSocketMessage(String message);

    /**
     * Called when a WebSocket has been closed
     *
     * @param code Status code indicating reason for closure.
     * @param reason String explaining the reason for closure in human-readable terms.
     * @param remote Indicates whether the closure was from the remote endpoint.
     */
    void onWebSocketClose(int code, String reason, boolean remote);

    void onWebSocketError(Exception ex);
}
