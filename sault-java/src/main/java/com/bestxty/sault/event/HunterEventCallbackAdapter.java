package com.bestxty.sault.event;

import com.bestxty.sault.event.hunter.HunterCancelEvent;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterPauseEvent;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.event.hunter.HunterResumeEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class HunterEventCallbackAdapter extends SimpleEventDispatcher {

    private HunterEventDispatcher hunterEventDispatcher;

    public HunterEventCallbackAdapter(EventCallbackExecutor eventCallbackExecutor) {
        super(eventCallbackExecutor);
    }


    public void setHunterEventDispatcher(HunterEventDispatcher hunterEventDispatcher) {
        if (hunterEventDispatcher == null) {
            throw new IllegalArgumentException("");
        }
        this.hunterEventDispatcher = hunterEventDispatcher;
        registerCallbacks();
    }

    private void registerCallbacks() {
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterStartEvent>() {
            @Override
            public void onEvent(HunterStartEvent event) {
                performHunterStart(event);
            }
        });
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterPauseEvent>() {
            @Override
            public void onEvent(HunterPauseEvent event) {
                performHunterPause(event);
            }
        });
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterCancelEvent>() {
            @Override
            public void onEvent(HunterCancelEvent event) {
                performHunterCancel(event);
            }
        });
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterResumeEvent>() {
            @Override
            public void onEvent(HunterResumeEvent event) {
                performHunterResume(event);
            }
        });
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterCompleteEvent>() {
            @Override
            public void onEvent(HunterCompleteEvent event) {
                performHunterComplete(event);
            }
        });
        hunterEventDispatcher.addEventCallback(new EventCallback<HunterProgressEvent>() {
            @Override
            public void onEvent(HunterProgressEvent event) {
                performHunterProgress(event);
            }
        });

    }

    abstract void performHunterStart(HunterStartEvent event);

    abstract void performHunterPause(HunterPauseEvent event);

    abstract void performHunterCancel(HunterCancelEvent event);

    abstract void performHunterResume(HunterResumeEvent event);

    abstract void performHunterComplete(HunterCompleteEvent event);

    abstract void performHunterProgress(HunterProgressEvent event);

}
