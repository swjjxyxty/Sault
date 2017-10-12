package com.bestxty.sault;

import android.net.NetworkInfo;

import com.bestxty.sault.Sault.Priority;

import java.util.concurrent.Future;

import static java.lang.Thread.currentThread;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
abstract class BaseSaultTaskHunter implements TaskHunter {

    private final int sequence;

    private final Sault sault;

    protected final Task task;

    final Downloader downloader;

    final Dispatcher dispatcher;

    private Exception exception;
    private Future<?> future;
    private int retryCount;


    BaseSaultTaskHunter(Sault sault,
                        Dispatcher dispatcher,
                        Task task,
                        Downloader downloader) {
        this.sault = sault;
        this.dispatcher = dispatcher;
        this.task = task;
        this.downloader = downloader;
        this.sequence = Utils.generateHunterSequence();
        this.retryCount = downloader.getRetryCount();
    }


    void updateThreadName() {
        currentThread().setName(Utils.getHunterThreadName(task));
    }

    abstract void hunter();

    @Override
    public final void run() {

        try {
            updateThreadName();
            hunter();
        } finally {
            currentThread().setName(Utils.THREAD_IDLE_NAME);
        }
    }

    protected void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public boolean isNeedResume() {
        return task.getProgress().getFinishedSize() != 0
                && task.isBreakPointEnabled()
                && downloader.supportBreakPoint();
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
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        boolean hasRetries = retryCount > 0;
        if (!hasRetries) {
            return false;
        }
        retryCount--;
        return downloader.shouldRetry(airplaneMode, info);
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
    public Priority getPriority() {
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
