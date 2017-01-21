package com.bestxty.dl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;

import static com.bestxty.dl.Utils.closeQuietly;
import static com.bestxty.dl.Utils.createTargetFile;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/21. for DownloadLibrary
 */
class DownloadThread implements Runnable {

    private static final int EOF = -1;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private final Task task;
    private final Downloader downloader;
    private final DownloadThreadInfo threadInfo;
    private final DownloadThreadStatusListener threadStatusListener;

    DownloadThread(Task task,
                   Downloader downloader,
                   DownloadThreadInfo threadInfo,
                   DownloadThreadStatusListener threadStatusListener) {
        this.task = task;
        this.downloader = downloader;
        this.threadInfo = threadInfo;
        this.threadStatusListener = threadStatusListener;
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void run() {
        try {
            hunt();
            threadStatusListener.onFinish(threadInfo);
        } catch (InterruptedIOException e) {
            threadStatusListener.onInterrupt(threadInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hunt() throws IOException {


        Downloader.Response response = downloader.load(task.getUri(),
                threadInfo.startPosition,
                threadInfo.endPosition);

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
            createTargetFile(task.getTarget());

            RandomAccessFile output = new RandomAccessFile(task.getTarget(), "rw");
            output.seek(threadInfo.startPosition);


            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int length;
                while (EOF != (length = stream.read(buffer))) {
                    output.write(buffer, 0, length);
                    threadStatusListener.onProgress(length);
                }

                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(stream);
        }


    }


    static class DownloadThreadInfo {
        final long startPosition;
        final long endPosition;
        final long threadId;


        private DownloadThreadInfo(long startPosition, long endPosition, long threadId) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.threadId = threadId;
        }

        static DownloadThreadInfo create(long startPosition, long endPosition, long threadId) {
            return new DownloadThreadInfo(startPosition, endPosition, threadId);
        }


    }


}
