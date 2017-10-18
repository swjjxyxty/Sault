package com.bestxty.sault;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;

import com.bestxty.sault.task.SaultTask;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public final class Utils {

    private static final String THREAD_PREFIX = "Sault-";
    public static final String DISPATCHER_THREAD_NAME = THREAD_PREFIX + "Dispatcher";
    public static final String THREAD_IDLE_NAME = THREAD_PREFIX + "Idle";


    public static final int EOF = -1;

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s

    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void log(String tag, String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
    }


    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void createTargetFile(File file) throws IOException {
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

    private static final AtomicInteger TASK_ID_GENERATOR = new AtomicInteger();
    private static final AtomicInteger HUNTER_SEQUENCE_GENERATOR = new AtomicInteger();

    public static int generateTaskId() {
        return TASK_ID_GENERATOR.incrementAndGet();
    }

    public static int generateHunterSequence() {
        return HUNTER_SEQUENCE_GENERATOR.incrementAndGet();
    }

    private static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
    private static final int KEY_PADDING = 50; // Determined by exact science.
    private static final char KEY_SEPARATOR = '\n';


    public static String generateTaskKey(SaultTask task) {
        String key = generateTaskKey0(MAIN_THREAD_KEY_BUILDER, task);
        MAIN_THREAD_KEY_BUILDER.setLength(0);
        return key;
    }

    private static String generateTaskKey0(StringBuilder builder, SaultTask task) {
        String path = task.getUri().toString();
        builder.ensureCapacity(path.length() + KEY_PADDING);
        builder.append(path);

        builder.append(KEY_SEPARATOR);
        builder.append("target:").append(task.getTarget().getPath());

        builder.append(KEY_SEPARATOR);
        builder.append("tag:").append(task.getTag());

        builder.append(KEY_SEPARATOR);
        builder.append("priority:").append(task.getPriority().name());

        builder.append(KEY_SEPARATOR);
        builder.append("breakPointEnable:").append(task.isBreakPointEnabled());

        builder.append(KEY_SEPARATOR);
        builder.append("hasCallback:").append(task.getCallback() != null);


        return builder.toString();
    }

    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(Utils.THREAD_PREFIX);
        }
    };


    public static String getHunterThreadName(SaultTask task) {
        String name = task.getKey() + "-" + task.getId();
        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
        builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);
        return builder.toString();
    }

}
