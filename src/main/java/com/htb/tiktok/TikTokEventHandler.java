package com.htb.tiktok;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TikTokEventHandler {

    private final ConcurrentLinkedQueue<TikTokEvent> eventQueue;

    public TikTokEventHandler() {
        this.eventQueue = new ConcurrentLinkedQueue<>();
    }

    public void queueEvent(TikTokEvent event) {
        eventQueue.offer(event);
    }

    public ConcurrentLinkedQueue<TikTokEvent> getEventQueue() {
        return eventQueue;
    }
}
