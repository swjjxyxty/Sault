package com.bestxty.sault.task;

import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public class PartedSaultTask extends DefaultSaultTask {

    private final long startPosition;
    private final long endPosition;
    private final SaultTask task;
    private final SaultTaskEventDispatcher eventDispatcher;

    public PartedSaultTask(SaultTask task, SaultTaskEventDispatcher eventDispatcher,
                           long startPosition, long endPosition) {
        super(task.getSault(), task.getTag(), task.getUri(), task.getCallback(),
                task.getTarget(), task.getPriority(), task.isBreakPointEnabled());
        this.task = task;
        this.eventDispatcher = eventDispatcher;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }


    @Override
    public Progress getProgress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyFinishedSize(long stepSize) {
        task.notifyFinishedSize(stepSize);
        eventDispatcher.dispatchSaultTaskProgress(task);
    }
}
