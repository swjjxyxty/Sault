package com.bestxty.sault;

import android.net.Uri;

import java.io.File;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public final class TaskWrapper {
    private final Task task;
    private final long startPosition;
    private final long endPosition;
    private final long totalSize;

    public TaskWrapper(Task task, long startPosition, long endPosition, long totalSize) {
        this.task = task;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.totalSize = totalSize;
    }

    public Sault getSault() {
        return task.getSault();
    }

    public int getId() {
        return task.getId();
    }

    public String getKey() {
        return task.getKey();
    }

    public Object getTag() {
        return task.getTag();
    }

    public Uri getUri() {
        return task.getUri();
    }

    public File getTarget() {
        return task.getTarget();
    }

    public Callback getCallback() {
        return task.getCallback();
    }

    public Sault.Priority getPriority() {
        return task.getPriority();
    }

    public boolean isBreakPointEnabled() {
        return task.isBreakPointEnabled();
    }

    public Task.Trace getTrace() {
        return task.getTrace();
    }

    public Task.Progress getProgress() {
        return task.getProgress();
    }


}
