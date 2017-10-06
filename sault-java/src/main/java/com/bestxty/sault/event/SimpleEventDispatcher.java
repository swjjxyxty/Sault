package com.bestxty.sault.event;

import com.bestxty.sault.utils.ClassUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class SimpleEventDispatcher implements EventDispatcher {

    private Map<Class<?>, List<EventCallback<?>>> eventCallbackMap = new ConcurrentHashMap<>(5);
    private List<EventCallback<?>> unresolvedEventCallbacks = new CopyOnWriteArrayList<>();
    private final AtomicInteger callbackCounter = new AtomicInteger();

    private EventCallbackExecutor eventCallbackExecutor;

    public SimpleEventDispatcher(EventCallbackExecutor eventCallbackExecutor) {
        this.eventCallbackExecutor = eventCallbackExecutor;
    }


    private List<EventCallback<?>> getEventCallbackContainer(EventCallback<?> callback) {
        Class<?> eventType = ClassUtils.getEventType(callback);
        if (eventType == null) return unresolvedEventCallbacks;
        List<EventCallback<?>> callbacks = eventCallbackMap.get(eventType);
        if (callbacks == null) {
            callbacks = new CopyOnWriteArrayList<>();
            eventCallbackMap.put(eventType, callbacks);
        }

        return callbacks;
    }

    private List<EventCallback<?>> getReadOnlyEventCallbackContainer(Class<?> eventType) {
        List<EventCallback<?>> callbacks = eventCallbackMap.get(eventType);
        if (callbacks == null) {
            callbacks = new CopyOnWriteArrayList<>();
            eventCallbackMap.put(eventType, callbacks);
        }
        return Collections.unmodifiableList(new CopyOnWriteArrayList<>(callbacks));
    }

    @Override
    public EventCallbackExecutor getEventCallbackExecutor() {
        return eventCallbackExecutor;
    }

    @Override
    public void addEventCallback(EventCallback<?> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("");
        }
        List<EventCallback<?>> callbacks = getEventCallbackContainer(callback);

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            callbackCounter.incrementAndGet();
        }
    }

    @Override
    public void removeEventCallback(EventCallback<?> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("");
        }
        List<EventCallback<?>> callbacks = getEventCallbackContainer(callback);
        if (callbacks.remove(callback)) {
            callbackCounter.decrementAndGet();
        }
    }

    @Override
    public void removeAllCallbacks() {
        eventCallbackMap.clear();
        callbackCounter.set(0);
    }


    @Override
    public int callbackSize() {
        return callbackCounter.get();
    }

    @Override
    public <E extends Event> void dispatcherEvent(E event) {
        List<EventCallback<?>> callbacks = getReadOnlyEventCallbackContainer(event.getClass());
        List<EventCallback<?>> unresolvedEventCallbacks =
                Collections.unmodifiableList(new CopyOnWriteArrayList<>(this.unresolvedEventCallbacks));
        for (EventCallback<?> callback : callbacks) {
            invokeCallback(callback, event);
        }
        for (EventCallback<?> unresolvedEventCallback : unresolvedEventCallbacks) {
            invokeCallback(unresolvedEventCallback, event);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void invokeCallback(EventCallback callback, Event event) {
        if (eventCallbackExecutor != null) {
            eventCallbackExecutor.execute(new ExecuteEventCallbackTask(callback, event));
        }
    }

}
