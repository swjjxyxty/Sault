package com.bestxty.dl;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bestxty.dl.Sault.Priority;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
final class Task {


    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    int id;
    long startTime;
    long endTime;
    long finishedSize;
    long totalSize;
    private final Object tag;
    private final File target;
    private final Callback callback;
    private final Priority priority;
    private final Uri uri;
    private final String key;
    private final Sault sault;
    private final boolean multiThreadEnabled;
    private final boolean breakPointEnabled;
    private final List<Task> subTaskList;
    private final long startPosition;
    private final long endPosition;

    Task(Sault sault, String key, Uri uri, File target, Object tag, Priority priority,
         Callback callback, boolean multiThreadEnabled, boolean breakPointEnabled) {
        this(sault, key, uri, target, tag, priority, callback, multiThreadEnabled, breakPointEnabled,
                0L, 0L);
    }

    Task(Sault sault, String key, Uri uri, File target, Object tag, Priority priority,
         Callback callback, boolean multiThreadEnabled, boolean breakPointEnabled,
         long startPosition, long endPosition) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.sault = sault;
        this.key = key;
        this.uri = uri;
        this.target = target;
        this.tag = tag;
        this.priority = priority;
        this.callback = callback;
        this.multiThreadEnabled = multiThreadEnabled;
        this.breakPointEnabled = breakPointEnabled;
        this.subTaskList = new ArrayList<>();
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    String getName() {
        if (uri != null) {
            return String.valueOf(uri.getPath());
        }
        return String.valueOf(id);
    }

    boolean isMultiThreadEnabled() {
        return multiThreadEnabled;
    }

    boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    Object getTag() {
        return tag;
    }

    File getTarget() {
        return target;
    }

    Callback getCallback() {
        return callback;
    }

    Priority getPriority() {
        return priority;
    }

    Uri getUri() {
        return uri;
    }

    String getKey() {
        return key;
    }

    Sault getSault() {
        return sault;
    }

    long getStartPosition() {
        return startPosition;
    }

    long getEndPosition() {
        return endPosition;
    }


    List<Task> getSubTaskList() {
        return subTaskList;
    }

    boolean isDone() {
        return finishedSize == (endPosition - startPosition) + 1;
    }

}
