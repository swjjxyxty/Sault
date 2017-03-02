package com.bestxty.sault;

import android.net.NetworkInfo;
import android.util.Log;

import com.bestxty.sault.Sault.Priority;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.sault.Utils.log;
import static java.lang.Thread.currentThread;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
abstract class BaseSaultTaskHunter implements TaskHunter {

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(Utils.THREAD_PREFIX);
        }
    };

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
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.retryCount = downloader.getRetryCount();
    }


    boolean isNeedResume() {
        return task.finishedSize != 0
                && task.isBreakPointEnabled()
                && downloader.supportBreakPoint();
    }


    void updateThreadName() {

        String name = task.getName() + "-" + task.id;

        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
        builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);

        log(builder.toString());

        currentThread().setName(builder.toString());
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
