package com.bestxty.dl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
final class Utils {

    static final String THREAD_PREFIX = "Download-";
    static final String DISPATCHER_THREAD_NAME = "Dispatcher";
    static final String THREAD_IDLE_NAME = THREAD_PREFIX + "Idle";


    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s

    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    static class ProgressInformer {
        Object tag;
        long totalSize;
        long finishedSize;
        private Callback callback;

        ProgressInformer(Object tag,
                         Callback callback) {
            this.tag = tag;
            this.callback = callback;
        }


        private ProgressInformer(ProgressInformer informer) {
            this.tag = informer.tag;
            this.totalSize = informer.totalSize;
            this.finishedSize = informer.finishedSize;
            this.callback = informer.callback;
        }

        static ProgressInformer from(ProgressInformer informer) {
            return new ProgressInformer(informer);
        }


        void notifyProgress() {
            if (callback != null) {
                callback.onProgress(tag, totalSize, finishedSize);
            }
        }
    }


    static class EventInformer {
        int event;
        Task task;
        private Callback callback;

        EventInformer(Task task, int event, Callback callback) {
            this.task = task;
            this.event = event;
            this.callback = callback;
        }

        static EventInformer fromTask(Task task, int event) {
            return new EventInformer(task, event, task.getCallback());
        }

        void notifyEvent() {
            if (callback != null) {
                callback.onEvent(task.getTag(), event);
            }
        }
    }

    static class DownloadThreadFactory implements ThreadFactory {
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
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
