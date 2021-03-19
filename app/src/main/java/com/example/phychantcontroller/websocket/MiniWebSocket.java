package com.example.phychantcontroller.websocket;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class MiniWebSocket extends WebSocketServer {

    private static final String TAG = "MiniWebSocket";
    private WebSocketManager webSocketManager;

    public MiniWebSocket(WebSocketManager webSocketManager, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        webSocketManager.addWebSocket(conn);
        Log.i(TAG, "Some one Connected...");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        webSocketManager.removeWebSocket(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        webSocketManager.sendMessage("received: " + message);
        Log.i(TAG, "OnMessage: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        webSocketManager.removeWebSocket(conn);
        Log.i(TAG, "Socket Exception: " + ex.toString());
    }

    @Override
    public void onStart() {
        Log.i(TAG, "Start WebSocket Server Success");
    }
}