package com.example.phychantcontroller.websocket;

import android.util.Log;

import com.example.phychantcontroller.Coordinate;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private MiniWebSocket miniWebSocketServer = null;
    private WebSocket webSocket = null;
    private Boolean isStarted = false;

    private JSONObject coordinateJSON = new JSONObject();

    public WebSocketManager() {

    }

    private static class SingleInstance {
        private static WebSocketManager INSTANCE = new WebSocketManager();
    }

    public static WebSocketManager getInstance() {
        return WebSocketManager.SingleInstance.INSTANCE;
    }

    public void addWebSocket(WebSocket socket) {
        if(webSocket != null && !webSocket.isClosed()) {
            webSocket.close();
            webSocket = null;
        }
        webSocket = socket;
        Log.i(TAG, "addWebSocket");
    }

    public void removeWebSocket(WebSocket socket) {
        if(!socket.isClosed()) {
            socket.close();
            socket = null;
        }
        if(!webSocket.isClosed()) {
            webSocket.close();
            webSocket = null;
        }
        Log.i(TAG, "removeWebSocket");
    }

    public void sendMessage(String message) {
        if(webSocket != null) {
            try {
                webSocket.send(message);
                Log.i(TAG, "message: " + message);
            } catch (Exception e) {
                Log.i(TAG, "Send Exception: " + e);
            }
        }
    }

    public void sendCoordinate(Coordinate coordinate) {
        if(webSocket != null) {
            try {
                coordinateJSON.put("x", coordinate.getX());
                coordinateJSON.put("y", coordinate.getY());
                webSocket.send(coordinateJSON.toString());
                Log.i(TAG, "message: " + coordinateJSON.toString());
            } catch (Exception e) {
                Log.i(TAG, "Send Exception: " + e);
            }
        }
    }

    public boolean start() {
        if (!isStarted) {
            try {
                miniWebSocketServer = new MiniWebSocket(this, 50050);
                miniWebSocketServer.start();
                isStarted = true;
                Log.i(TAG,"Start MiniWebSocket Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"Start MiniWebSocket Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean stop() {
        if (isStarted && miniWebSocketServer != null) {
            try {
                miniWebSocketServer.stop();
                miniWebSocketServer = null;
                isStarted = false;
                Log.i(TAG, "Stop MiniWebSocket Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Stop MiniWebSocket Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isServerStarted() { return isStarted; }
}
