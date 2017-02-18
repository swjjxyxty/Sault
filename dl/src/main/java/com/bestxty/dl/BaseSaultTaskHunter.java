package com.bestxty.dl;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
abstract class BaseSaultTaskHunter implements TaskHunter {

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private final int sequence;

    private final Sault sault;

    protected final Task task;

    protected final Downloader downloader;

    final Dispatcher dispatcher;

    private Exception exception;
    private Future<?> future;

    BaseSaultTaskHunter(Sault sault,
                        Dispatcher dispatcher,
                        Task task,
                        Downloader downloader) {
        this.sault = sault;
        this.dispatcher = dispatcher;
        this.task = task;
        this.downloader = downloader;
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
    }


    boolean isNeedResume() {
        return task.finishedSize != 0
                && task.isBreakPointEnabled()
                && downloader.supportBreakPoint();
    }

    protected void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public boolean cancel() {
        return future != null && (future.isDone() || future.cancel(true));
    }

    @Override
    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }


    @Override
    public void setFuture(Future<?> future) {
        this.future = future;
    }

    @Override
    public String getKey() {
        return task.getKey();
    }

    @Override
    public Sault.Priority getPriority() {
        return task.getPriority();
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public Sault getSault() {
        return sault;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public int getSequence() {
        return sequence;
    }
}
