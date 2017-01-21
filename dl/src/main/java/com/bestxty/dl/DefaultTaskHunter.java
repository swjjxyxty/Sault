package com.bestxty.dl;


import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.dl.Downloader.ContentLengthException;
import static com.bestxty.dl.Downloader.Response;
import static com.bestxty.dl.Downloader.ResponseException;
import static com.bestxty.dl.Sault.Priority;
import static com.bestxty.dl.Utils.ProgressInformer;
import static com.bestxty.dl.Utils.THREAD_IDLE_NAME;
import static com.bestxty.dl.Utils.closeQuietly;
import static com.bestxty.dl.Utils.createTargetFile;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
class DefaultTaskHunter implements TaskHunter {

    private static final int EOF = -1;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    final int sequence;

    private final Priority priority;
    private final Sault sault;
    private final String key;
    private final Object tag;
    private final Task task;
    private final Downloader downloader;
    private final Dispatcher dispatcher;

    Future<?> future;

    private Exception exception;


    private int retryCount;

    DefaultTaskHunter(Sault sault,
                      Dispatcher dispatcher,
                      Task task,
                      Downloader downloader) {
        this.sault = sault;
        this.dispatcher = dispatcher;
        this.task = task;
        this.downloader = downloader;
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.priority = task.getPriority();
        this.key = task.getKey();
        this.tag = task.getTag();
        this.retryCount = downloader.getRetryCount();
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void run() {
        System.out.println("hunter running");

        try {
            File result = hunt();
            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            exception = e;
            dispatcher.dispatchFailed(this);
        } catch (ResponseException e) {
            e.printStackTrace();
            exception = e;
            dispatcher.dispatchFailed(this);
        } catch (ContentLengthException e) {
            e.printStackTrace();
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
            dispatcher.dispatchFailed(this);
        } finally {
            Thread.currentThread().setName(THREAD_IDLE_NAME);
        }
    }

    private File hunt() throws IOException {

        boolean needResume = task.finishedSize != 0
                && task.isBreakPointEnabled()
                && downloader.supportBreakPoint();

        Response response = needResume ? downloader.load(task.getUri(), task.finishedSize)
                : downloader.load(task.getUri());

        InputStream stream = response.stream;
        if (stream == null) {
            System.out.println("stream is null");
            return null;
        }

        if (response.contentLength == 0) {
            closeQuietly(stream);
            throw new ContentLengthException("Received response with 0 content-length header.");
        }
        try {
            createTargetFile(task.getTarget());

            RandomAccessFile output = new RandomAccessFile(task.getTarget(), "rw");

            ProgressInformer progress = new ProgressInformer(task.getTag(), task.getCallback());

            if (needResume) {
                output.seek(task.finishedSize);
                progress.totalSize = task.totalSize;
            } else {
                progress.totalSize = response.contentLength;
                task.totalSize = response.contentLength;
            }

            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int length;
                while (EOF != (length = stream.read(buffer))) {
                    output.write(buffer, 0, length);
                    task.finishedSize += length;
                    progress.finishedSize = task.finishedSize;
                    dispatcher.dispatchProgress(progress);
                }

                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(stream);
        }

        task.endTime = System.nanoTime();

        return task.getTarget();
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
    public void setFuture(Future future) {
        this.future = future;
    }


    boolean supportsReplay() {
        return downloader.supportsReplay();
    }

    void attach(Task task) {

    }

    void detach(Task task) {

    }

    public boolean cancel() {
        return future != null
                && future.cancel(true);
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    public Exception getException() {
        return exception;
    }

    public Sault getSault() {
        return sault;
    }

    public String getKey() {
        return key;
    }

    Object getTag() {
        return tag;
    }

    public Task getTask() {
        return task;
    }

    Downloader getDownloader() {
        return downloader;
    }

    Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Priority getPriority() {
        return priority;
    }

}
