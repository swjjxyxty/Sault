package com.bestxty.dl;


import android.net.NetworkInfo;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.dl.Sault.Priority;
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

    Exception exception;

    File result;
    int retryCount;

    public TaskHunter(Sault sault, Dispatcher dispatcher, Task task, Downloader downloader) {
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

        try {
            result = hunt();
            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (InterruptedIOException e) {
            exception = e;
            dispatcher.dispatchFailed(this);
        } catch (Downloader.ResponseException e) {
            exception = e;
            dispatcher.dispatchFailed(this);
        } catch (Downloader.ContentLengthException e) {
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (IOException e) {
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (Exception e) {
            exception = e;
            dispatcher.dispatchFailed(this);
        } finally {
            Thread.currentThread().setName(THREAD_IDLE_NAME);
        }
    }

    private File hunt() throws IOException {
        Downloader.Response response = downloader.load(task.getUri());

        InputStream stream = response.stream;
        if (stream == null) {
            return null;
        }

        if (response.contentLength == 0) {
            closeQuietly(stream);
            throw new Downloader.ContentLengthException("Received response with 0 content-length header.");
        }
        try {

            FileOutputStream output = openOutputStream(task.getTarget());
//            Progress progress = new Progress(task.getTag(), task.getCallback());
//            progress.totalSize = response.contentLength;
            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n = 0;
                while (EOF != (n = stream.read(buffer))) {
                    output.write(buffer, 0, n);
//                    progress.finishedSize += n;
//                    dispatcher.dispatchProgress(progress);
                }

                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(stream);
        }
        Log.d("TaskHunter", task.getTarget().getAbsolutePath());

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


    private static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
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


    private static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }


    private static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
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
        return new FileOutputStream(file, append);
    }
}
