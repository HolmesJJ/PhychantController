package com.example.phychantcontroller.websocket;

import android.util.Log;

import com.example.phychantcontroller.utils.ToastUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MiniWebSocket extends WebSocketServer {

    private static final String TAG = "MiniWebSocket";
    private WebSocketManager webSocketManager;

    public MiniWebSocket(WebSocketManager webSocketManager, int port) {
        super(new InetSocketAddress(port));
        this.webSocketManager = webSocketManager;
    }

    public MiniWebSocket(WebSocketManager webSocketManager, InetSocketAddress address) {
        super(address);
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        webSocketManager.addWebSocket(conn);
        Log.i(TAG, "Some one Connected...");
        ToastUtils.showShortSafe("WS started");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        webSocketManager.removeWebSocket(conn);
        ToastUtils.showShortSafe("WS closed");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        webSocketManager.onMessage("received: " + message);
        Log.i(TAG, "OnMessage: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        webSocketManager.removeWebSocket(conn);
        ToastUtils.showShortSafe("WS error " + ex.getMessage());
        Log.i(TAG, "Socket Exception: " + ex.toString());
    }

    @Override
    public void onStart() {
        Log.i(TAG, "Start WebSocket Server Success");
    }
}