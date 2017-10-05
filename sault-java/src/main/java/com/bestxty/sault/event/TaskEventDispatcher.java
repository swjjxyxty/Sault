package com.bestxty.sault.event;

import com.bestxty.sault.event.hunter.HunterCancelEvent;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterPauseEvent;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.event.hunter.HunterResumeEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class TaskEventDispatcher extends HunterEventCallbackAdapter {


    public TaskEventDispatcher(EventCallbackExecutor eventCallbackExecutor) {
        super(eventCallbackExecutor);
    }

    @Override
    void performHunterStart(HunterStartEvent event) {
        System.out.println("event = " + event);
    }

    @Override
    void performHunterPause(HunterPauseEvent event) {

    }

    @Override
    void performHunterCancel(HunterCancelEvent event) {

    }

    @Override
    void performHunterResume(HunterResumeEvent event) {

    }

    @Override
    void performHunterComplete(HunterCompleteEvent event) {

    }

    @Override
    void performHunterProgress(HunterProgressEvent event) {
        System.out.println("event.getProgress() = " + event.getProgress());
    }

}
