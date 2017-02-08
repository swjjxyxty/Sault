package com.bestxty.dl;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.bestxty.dl.Sault.Priority;
import static com.bestxty.dl.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
final class Task {

    private static final int LENGTH_PER_THREAD = 1024 * 1024 * 10;

    private static final AtomicLong SUB_TASK_ID_GENERATOR = new AtomicLong();

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
    private final List<SubTask> subTaskList;

    Task(Sault sault, String key, Uri uri, File target, Object tag, Priority priority,
         Callback callback, boolean multiThreadEnabled, boolean breakPointEnabled) {
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

    boolean isNeedResume() {
        return breakPointEnabled;
    }

    void splitTask() {

        long threadSize;
        long threadLength = LENGTH_PER_THREAD;
        if (totalSize <= LENGTH_PER_THREAD) {
            threadSize = 2;
            threadLength = totalSize / threadSize;
        } else {
            threadSize = totalSize / LENGTH_PER_THREAD;
        }
        log(threadSize + "------x");
        long remainder = totalSize % threadLength;
        for (int i = 0; i < threadSize; i++) {
            long start = i * threadLength;
            long end = start + threadLength - 1;
            if (i == threadSize - 1) {
                end = start + threadLength + remainder - 1;
            }
            long subTaskId = SUB_TASK_ID_GENERATOR.incrementAndGet();
            subTaskList.add(SubTask.create(start, end, subTaskId));
        }
    }


    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    static class SubTask {
        private final long startPosition;
        private final long endPosition;
        private final long threadId;

        long startTime;
        long endTime;

        long finishedSize;

        private SubTask(long startPosition, long endPosition, long threadId) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.threadId = threadId;
        }

        public long getStartPosition() {
            return startPosition;
        }

        public long getEndPosition() {
            return endPosition;
        }

        static SubTask create(long startPosition, long endPosition, long threadId) {
            return new SubTask(startPosition, endPosition, threadId);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"threadId\":")
                    .append(threadId);
            sb.append(",\"startPosition\":")
                    .append(startPosition);
            sb.append(",\"endPosition\":")
                    .append(endPosition);
            sb.append('}');
            return sb.toString();
        }
    }
}
