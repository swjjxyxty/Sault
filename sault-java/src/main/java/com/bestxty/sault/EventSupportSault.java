package com.bestxty.sault;

import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.EventCallbackExecutor;
import com.bestxty.sault.event.EventDispatcher;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class EventSupportSault implements EventDispatcher {

    private EventDispatcher eventDispatcher;

    public EventSupportSault(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public EventCallbackExecutor getEventCallbackExecutor() {
        return eventDispatcher.getEventCallbackExecutor();
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
}
