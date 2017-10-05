package com.bestxty.sault.hunter;

import com.bestxty.sault.CancelableHunter;

import java.util.concurrent.Future;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class SimpleCancelableHunter implements CancelableHunter {


    private Future<?> future;

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
    public boolean isDone() {
        return future != null && future.isDone();
    }
}
