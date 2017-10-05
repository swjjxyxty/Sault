package com.bestxty.sault;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class SimpleSaultExecutorService extends ThreadPoolExecutor implements SaultExecutorService {

    private static final int DEFAULT_THREAD_COUNT = 3;

    SimpleSaultExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MICROSECONDS,
                new PriorityBlockingQueue<Runnable>(), new DownloadThreadFactory());
    }


    private static class DownloadThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        @Override
        public Thread newThread(Runnable r) {
            return new DownloadThread(r);
        }
    }

    private static class DownloadThread extends Thread {


        DownloadThread(Runnable runnable) {
            super(runnable);
        }

        @Override
        public void run() {
            super.run();
        }
    }
}
