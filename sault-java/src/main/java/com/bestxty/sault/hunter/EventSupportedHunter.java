package com.bestxty.sault.hunter;

import com.bestxty.sault.Hunter;
import com.bestxty.sault.Task;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.EventCallbackExecutor;
import com.bestxty.sault.event.EventDispatcher;
import com.bestxty.sault.event.SimpleEventDispatcher;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;
import com.bestxty.sault.event.task.TaskStartEvent;

import java.util.Collections;
import java.util.List;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public abstract class EventSupportedHunter extends SimpleRetryableHunter implements EventDispatcher {

    private EventDispatcher eventDispatcher;

    protected Task task;

    public EventSupportedHunter(Downloader downloader, Task task, EventCallbackExecutor eventCallbackExecutor) {
        super(downloader);
        this.task = task;
        this.eventDispatcher = new SimpleEventDispatcher(eventCallbackExecutor);
        registerTaskEventCallback();
    }

    private void registerTaskEventCallback() {
        List<EventCallback<?>> eventCallbacks = task.getEventCallbacks() == null
                ? Collections.<EventCallback<?>>emptyList() : task.getEventCallbacks();
        for (EventCallback<?> eventCallback : eventCallbacks) {
            task.addEventCallback(eventCallback);
        }
        addEventCallback(new EventCallback<HunterStartEvent>() {
            @Override
            public void onEvent(HunterStartEvent event) {
                task.dispatcherEvent(event);
            }
        });
        addEventCallback(new EventCallback<HunterProgressEvent>() {
            @Override
            public void onEvent(HunterProgressEvent event) {
                task.dispatcherEvent(event);
            }
        });
        addEventCallback(new EventCallback<HunterCompleteEvent>() {
            @Override
            public void onEvent(HunterCompleteEvent event) {
                task.dispatcherEvent(event);
            }
        });
    }

    @Override
    public Task getTask() {
        return task;
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
    public EventCallbackExecutor getEventCallbackExecutor() {
        return eventDispatcher.getEventCallbackExecutor();
    }
}
