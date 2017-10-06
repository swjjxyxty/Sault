package com.bestxty.sault.event;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public interface EventDispatcher {

    EventCallbackExecutor getEventCallbackExecutor();

    void addEventCallback(EventCallback<?> callback);

    void removeEventCallback(EventCallback<?> callback);

    void removeAllCallbacks();

    int callbackSize();

    <E extends Event> void dispatcherEvent(E event);

}
