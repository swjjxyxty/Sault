package com.bestxty.sault;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;


/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
final class Utils {

    private static final String TAG = "Sault";
    static final String THREAD_PREFIX = "Sault-";
    static final String DISPATCHER_THREAD_NAME = THREAD_PREFIX + "Dispatcher";
    static final String THREAD_IDLE_NAME = THREAD_PREFIX + "Idle";


    static final int EOF = -1;

    static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s

    static void log(String msg) {
        Log.d(TAG, msg);
    }

    static void log(String msg, Throwable throwable) {
        Log.e(TAG, msg, throwable);
    }


    static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    static void createTargetFile(File file) throws IOException {
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

    @SuppressWarnings("deprecation")
    static boolean isAirplaneModeOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } catch (NullPointerException e) {
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    interface Informer {
        void callNotify();
    }

    static class ErrorInformer implements Informer {
        private Callback callback;
        private SaultException exception;

        ErrorInformer(SaultException exception, Callback callback) {
            this.exception = exception;
            this.callback = callback;
        }

        static ErrorInformer create(Callback callback, SaultException exception) {
            return new ErrorInformer(exception, callback);
        }

        @Override
        public void callNotify() {
            if (callback != null) {
                callback.onError(exception);
            }
        }

    }


    static class ProgressInformer implements Informer {
        Object tag;
        long totalSize;
        long finishedSize;
        private Callback callback;

        private ProgressInformer(Object tag,
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

        static ProgressInformer create(ProgressInformer informer) {
            return new ProgressInformer(informer);
        }


        static ProgressInformer create(Task task) {
            return new ProgressInformer(task.getTag(), task.getCallback());
        }


        @Override
        public void callNotify() {
            if (callback != null) {
                callback.onProgress(tag, totalSize, finishedSize);
            }
        }

    }


    static class EventInformer implements Informer {
        int event;
        Task task;
        private Callback callback;

        private EventInformer(Task task, int event, Callback callback) {
            this.task = task;
            this.event = event;
            this.callback = callback;
        }

        static EventInformer create(Task task, int event) {
            return new EventInformer(task, event, task.getCallback());
        }

        @Override
        public void callNotify() {
            if (callback != null) {
                callback.onEvent(task.getTag(), event);
            }
        }

    }

    static class DownloadThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        @Override
        public Thread newThread(Runnable r) {
            return new DownloadThread(r);
        }
    }

    private static class DownloadThread extends Thread {


        DownloadThread(Runnable runnable) {
            super(runnable, THREAD_IDLE_NAME);
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
