package com.bestxty.dl;

import android.net.NetworkInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static com.bestxty.dl.DownloadThread.DownloadThreadInfo;
import static com.bestxty.dl.Sault.Priority;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/21. for DownloadLibrary
 */
class MultiThreadTaskHunter implements TaskHunter, DownloadThreadStatusListener {


    private static final int LENGTH_PER_THREAD = 1024 * 1024 * 10;

    private static final AtomicLong THREAD_ID_GENERATOR = new AtomicLong();

    private final Task task;
    private final Downloader downloader;
    private final Dispatcher dispatcher;
    private final Utils.ProgressInformer progress;

    private List<DownloadThreadInfo> downloadThreadInfoList;
    private long lastTime = 0L;

    MultiThreadTaskHunter(Task task,
                          Downloader downloader,
                          Dispatcher dispatcher) {
        this.task = task;
        this.downloader = downloader;
        this.dispatcher = dispatcher;
        downloadThreadInfoList = new ArrayList<>();
        progress = new Utils.ProgressInformer(task.getTag(), task.getCallback());
    }

    private long fetchSize() {
        try {
            return downloader.fetchContentLength(task.getUri());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2017/1/21 handle this exception.
            return 0L;
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void run() {

        long totalBytes = fetchSize();
        progress.totalSize = totalBytes;


        long threadSize;
        long threadLength = LENGTH_PER_THREAD;
        if (totalBytes <= LENGTH_PER_THREAD) {
            threadSize = 2;
            threadLength = totalBytes / threadSize;
        } else {
            threadSize = totalBytes / LENGTH_PER_THREAD;
        }
        long remainder = totalBytes % threadLength;
        for (int i = 0; i < threadSize; i++) {
            long start = i * threadLength;
            long end = start + threadLength - 1;
            if (i == threadSize - 1) {
                end = start + threadLength + remainder - 1;
            }
            DownloadThreadInfo threadInfo = DownloadThreadInfo
                    .create(start, end, THREAD_ID_GENERATOR.incrementAndGet());
            DownloadThread downloadThread = new DownloadThread(task, downloader, threadInfo, this);
            downloadThreadInfoList.add(threadInfo);
            dispatcher.dispatchDownloadThread(downloadThread);
        }
    }


    @Override
    public synchronized void onProgress(long length) {
        progress.finishedSize += length;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > 1000) {
            dispatcher.dispatchProgress(progress);
            lastTime = currentTime;
        }
    }

    @Override
    public synchronized void onFinish(DownloadThreadInfo threadInfo) {
        downloadThreadInfoList.remove(threadInfo);
        if (downloadThreadInfoList.isEmpty()) {
//            dispatcher.dispatchComplete();
        }
    }

    @Override
    public synchronized void onInterrupt(DownloadThreadInfo threadInfo) {
        downloadThreadInfoList.remove(threadInfo);
    }

    @Override
    public Sault getSault() {
        return null;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public String getKey() {
        return task.getKey();
    }

    @Override
    public int getSequence() {
        return 0;
    }

    @Override
    public Priority getPriority() {
        return null;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }

    @Override
    public void setFuture(Future future) {

    }
}
