package com.bestxty.sault.hunter;

import android.net.NetworkInfo;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.Utils;
import com.bestxty.sault.task.SaultTask;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public abstract class AbstractTaskHunter implements TaskHunter {

    private final SaultTask task;

    private final Downloader downloader;

    private final int sequence;

    private final AtomicInteger retryCount;

    private Future<?> future;

    public AbstractTaskHunter(SaultTask task, Downloader downloader) {
        this.task = task;
        this.downloader = downloader;
        this.sequence = Utils.generateHunterSequence();
        this.retryCount = new AtomicInteger(downloader.getRetryCount());
    }

    protected Downloader getDownloader() {
        return downloader;
    }

    @Override
    public Sault getSault() {
        return task.getSault();
    }

    @Override
    public SaultTask getTask() {
        return task;
    }

    @Override
    public Sault.Priority getPriority() {
        return task.getPriority();
    }

    @Override
    public int getSequence() {
        return sequence;
    }


    @Override
    public void setFuture(Future<?> future) {
        this.future = future;
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
    public boolean isNeedResume() {
        return task.getProgress().getFinishedSize() != 0
                && task.isBreakPointEnabled()
                && downloader.supportBreakPoint();
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        boolean hasRetries = retryCount.get() > 0;
        if (!hasRetries) {
            return false;
        }
        retryCount.decrementAndGet();
        return downloader.shouldRetry(airplaneMode, info);
    }

    abstract void hunter();

    @Override
    public final void run() {
        try {
            Thread.currentThread().setName(Utils.getHunterThreadName(task));
            hunter();
        } finally {
            Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
        }
    }
}
