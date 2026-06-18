package com.htb.tiktok;

public class TikTokEvent {
    private final String user;
    private final String type;
    private final long timestamp;

    public TikTokEvent(String user, String type) {
        this.user = user;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUser() { return user; }
    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + type + "] " + user;
    }
}
