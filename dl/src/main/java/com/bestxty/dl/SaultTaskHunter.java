package com.bestxty.dl;

import android.net.NetworkInfo;
import android.net.Uri;

import com.bestxty.dl.Downloader.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.dl.Utils.DEFAULT_BUFFER_SIZE;
import static com.bestxty.dl.Utils.EOF;
import static com.bestxty.dl.Utils.closeQuietly;
import static com.bestxty.dl.Utils.createTargetFile;
import static com.bestxty.dl.Utils.log;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/24. for DownloadLibrary
 */
class SaultTaskHunter implements TaskHunter {

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private final int sequence;
    private final Task task;
    private final Sault sault;
    private final Dispatcher dispatcher;
    private final Downloader downloader;

    private Exception exception;
    private Future<?> future;
    private List<InternalTaskHunter> taskHunterList;

    SaultTaskHunter(Sault sault,
                    Dispatcher dispatcher,
                    Task task,
                    Downloader downloader) {
        this.sault = sault;
        this.dispatcher = dispatcher;
        this.task = task;
        this.downloader = downloader;
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.taskHunterList = new ArrayList<>();
    }


    @Override
    public void run() {
        try {
            task.totalSize = downloader.fetchContentLength(task.getUri());

            task.splitTask();
            log(task.getSubTaskList().size()+"-----------list size");

            for (Task.SubTask subTask : task.getSubTaskList()) {
                log(subTask.toString());
                InternalTaskHunter taskHunter = new InternalTaskHunter(subTask, task.getUri(), downloader, task.getTarget());
                taskHunterList.add(taskHunter);
                Future future = dispatcher.submit(taskHunter);
                taskHunter.setFuture(future);
            }


        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
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
    public Exception getException() {
        return exception;
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
    public int getSequence() {
        return sequence;
    }

    @Override
    public boolean cancel() {
        return future != null
                && future.cancel(true);
    }

    @Override
    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }

    @Override
    public void setFuture(Future<?> future) {
        this.future = future;
    }


    private static class InternalTaskHunter implements Runnable {

        private final Task.SubTask subTask;
        private final Uri uri;
        private final Downloader downloader;
        private final File target;

        private Future<?> future;

        InternalTaskHunter(Task.SubTask subTask, Uri uri, Downloader downloader, File target) {
            this.subTask = subTask;
            this.uri = uri;
            this.downloader = downloader;
            this.target = target;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }

        public boolean cancel() {
            return future != null
                    && future.cancel(true);
        }

        public boolean isCancelled() {
            return future != null && future.isCancelled();
        }

        @Override
        public void run() {
            try {
                receiveContent(subTask);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void receiveContent(Task.SubTask subTask) throws IOException {
            final long startPosition = subTask.getStartPosition();
            final long endPosition = subTask.getEndPosition();

            Response response = downloader.load(uri, startPosition, endPosition);

            InputStream stream = response.stream;
            if (stream == null) {
                System.out.println("stream is null");
                return;
            }

            if (response.contentLength == 0) {
                closeQuietly(stream);
                throw new Downloader.ContentLengthException("Received response with 0 content-length header.");
            }
            try {
                createTargetFile(target);

                RandomAccessFile output = new RandomAccessFile(target, "rw");

                output.seek(startPosition);

                try {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int length;
                    while (EOF != (length = stream.read(buffer))) {
                        output.write(buffer, 0, length);
                        subTask.finishedSize += length;
                    }

                    output.close(); // don't swallow close Exception if copy completes normally
                } finally {
                    closeQuietly(output);
                }
            } finally {
                closeQuietly(stream);
                log("done."+subTask.finishedSize);
            }
        }

    }
}
