package com.bestxty.sault;

import android.net.Uri;

import java.io.File;

import static com.bestxty.sault.Sault.Priority;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
class Task {

    private final Sault sault;
    private final int id;
    private final String key;
    private final Object tag;
    private final Uri uri;
    private final File target;
    private final Callback callback;
    private final Priority priority;
    private final boolean breakPointEnabled;
    private final Trace trace;
    private Progress progress;

    public Task(Sault sault, int id, String key, Object tag,
                Uri uri, File target, Callback callback,
                Priority priority, boolean breakPointEnabled) {
        this.sault = sault;
        this.id = id;
        this.key = key;
        this.tag = tag;
        this.uri = uri;
        this.target = target;
        this.callback = callback;
        this.priority = priority;
        this.breakPointEnabled = breakPointEnabled;
        this.trace = new Trace();
    }

    public Sault getSault() {
        return sault;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Object getTag() {
        return tag;
    }

    public Uri getUri() {
        return uri;
    }

    public File getTarget() {
        return target;
    }

    public Callback getCallback() {
        return callback;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    public Trace getTrace() {
        return trace;
    }

    public Progress getProgress() {
        return progress;
    }

    public static class Trace {
        private final long createTime;
        private long startTime;
        private long endTime;

        private Trace() {
            createTime = System.currentTimeMillis();
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    public static class Progress {
        private final long totalSize;
        private long finishedSize;

        private Progress(long totalSize, long finishedSize) {
            this.totalSize = totalSize;
            this.finishedSize = finishedSize;
        }

        public boolean isDone() {
            return finishedSize == totalSize;
        }

        public long getFinishedSize() {
            return finishedSize;
        }
    }

}
