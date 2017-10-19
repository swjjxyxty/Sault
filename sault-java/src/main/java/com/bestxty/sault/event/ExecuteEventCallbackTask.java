package com.bestxty.sault.event;

import static com.bestxty.sault.utils.Utils.log;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public final class ExecuteEventCallbackTask implements Runnable {

    private EventCallback<? extends Event> eventCallback;

    private Event event;

    ExecuteEventCallbackTask(EventCallback<? extends Event> eventCallback,
                             Event event) {
        this.eventCallback = eventCallback;
        this.event = event;
    }

    @Override
    public final void run() {
//        log("invoke callback in :" + Thread.currentThread().getName());
        if (eventCallback != null && event != null) {
            invokeCallback(eventCallback, event);
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private void invokeCallback(EventCallback callback, Event event) {
        try {
//            log("invoke callback with: callback= " + callback + ",event= " + event);
            callback.onEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
