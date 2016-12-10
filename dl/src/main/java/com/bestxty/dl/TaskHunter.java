package com.bestxty.dl;


import android.net.NetworkInfo;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.dl.Downloader.ContentLengthException;
import static com.bestxty.dl.Downloader.Response;
import static com.bestxty.dl.Downloader.ResponseException;
import static com.bestxty.dl.Sault.Priority;
import static com.bestxty.dl.Utils.ProgressInformer;
import static com.bestxty.dl.Utils.THREAD_IDLE_NAME;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
class TaskHunter implements Runnable {

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

    TaskHunter(Sault sault,
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

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        boolean hasRetries = retryCount > 0;
        if (!hasRetries) {
            return false;
        }
        retryCount--;
        return downloader.shouldRetry(airplaneMode, info);
    }


    boolean supportsReplay() {
        return downloader.supportsReplay();
    }

    void attach(Task task) {

    }

    void detach(Task task) {

    }

    boolean cancel() {
        return future != null
                && future.cancel(true);
    }

    boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    Exception getException() {
        return exception;
    }

    Sault getSault() {
        return sault;
    }

    String getKey() {
        return key;
    }

    Object getTag() {
        return tag;
    }

    Task getTask() {
        return task;
    }

    Downloader getDownloader() {
        return downloader;
    }

    Dispatcher getDispatcher() {
        return dispatcher;
    }

    Priority getPriority() {
        return priority;
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }


    private static void createTargetFile(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
    }
}
