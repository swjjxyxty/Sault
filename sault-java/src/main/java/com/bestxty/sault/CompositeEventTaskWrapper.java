package com.bestxty.sault;

import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;
import com.bestxty.sault.event.task.TaskCompleteEvent;
import com.bestxty.sault.event.task.TaskProgressEvent;
import com.bestxty.sault.event.task.TaskStartEvent;
import com.bestxty.sault.utils.Utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xty
 *         <p>
 *         Created by xty on 2017/10/6.
 */
public class CompositeEventTaskWrapper extends TaskWrapper {

    private AtomicInteger startEventCounter;
    private AtomicInteger completeEventCounter;
    private AtomicLong progressCounter = new AtomicLong();
    private final long totalSize;
    private int lastProgress = 0;

    public CompositeEventTaskWrapper(Task task, List<SplitTask> splitTasks, long totalSize) {
        super(task, splitTasks);
        startEventCounter = new AtomicInteger(splitTasks.size());
        completeEventCounter = new AtomicInteger(splitTasks.size());
        this.totalSize = totalSize;
        registerEventCallback();
    }

    private EventCallback<HunterStartEvent> startEventCallback = new EventCallback<HunterStartEvent>() {
        @Override
        public void onEvent(HunterStartEvent event) {
            if (startEventCounter.decrementAndGet() == 0) {
                Hunter hunter = (Hunter) event.getSource();
                task.dispatcherEvent(new TaskStartEvent(hunter.getTask()));
            }
        }
    };

    private EventCallback<HunterProgressEvent> progressEventCallback = new EventCallback<HunterProgressEvent>() {
        @Override
        public void onEvent(HunterProgressEvent event) {
            long finishedSize = progressCounter.addAndGet(event.getFinishedSize());
            int progress = Utils.calculateProgress(finishedSize, totalSize);
            if (progress > lastProgress) {
                task.dispatcherEvent(new TaskProgressEvent(task, progress));
                lastProgress = progress;
            }
        }
    };


    private EventCallback<HunterCompleteEvent> completeEventCallback = new EventCallback<HunterCompleteEvent>() {
        @Override
        public void onEvent(HunterCompleteEvent event) {
            if (completeEventCounter.decrementAndGet() == 0) {
                Hunter hunter = (Hunter) event.getSource();
                task.dispatcherEvent(new TaskCompleteEvent(hunter.getTask()));
            }
        }
    };

    private void registerEventCallback() {
        for (SplitTask splitTask : getSplitTasks()) {
            splitTask.addEventCallback(startEventCallback);
            splitTask.addEventCallback(progressEventCallback);
            splitTask.addEventCallback(completeEventCallback);
        }
    }
}
