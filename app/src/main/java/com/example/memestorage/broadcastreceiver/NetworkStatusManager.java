package com.example.memestorage.broadcastreceiver;

public class NetworkStatusManager {
    private static NetworkStatusManager instance;
    private boolean isConnected;

    private NetworkStatusManager() {}

    public static synchronized NetworkStatusManager getInstance() {
        if (instance == null) {
            instance = new NetworkStatusManager();
        }
        return instance;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
        // Notify listeners or update views
    }
}
