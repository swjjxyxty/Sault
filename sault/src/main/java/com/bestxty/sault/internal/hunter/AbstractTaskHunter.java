package com.bestxty.sault.internal.hunter;

import android.net.NetworkInfo;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.task.SaultTask;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

abstract class AbstractTaskHunter implements TaskHunter {

    static final String TAG = "TaskHunter";

    private final SaultTask task;

    @Inject
    Downloader downloader;

    @Inject
    @Named("hunterSequence")
    int sequence;

    @Inject
    @Named("retryCount")
    AtomicInteger retryCount;

    private Future<?> future;

    AbstractTaskHunter(SaultTask task) {
        this.task = task;
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
