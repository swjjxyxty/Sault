package com.bestxty.sault.hunter;

import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.EventDispatcher;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public abstract class EventSupportedHunter extends SimpleRetryableHunter implements EventDispatcher {

    private EventDispatcher eventDispatcher;

    public EventSupportedHunter(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void addEventCallback(EventCallback<?> callback) {
        eventDispatcher.addEventCallback(callback);
    }

    @Override
    public void removeEventCallback(EventCallback<?> callback) {
        eventDispatcher.removeEventCallback(callback);
    }

    @Override
    public void removeAllCallbacks() {
        eventDispatcher.removeAllCallbacks();
    }

    @Override
    public int callbackSize() {
        return eventDispatcher.callbackSize();
    }


    @Override
    public <E extends Event> void dispatcherEvent(E event) {
        eventDispatcher.dispatcherEvent(event);
    }

    @Override
    public void stop() {
        eventDispatcher.stop();
    }
}
