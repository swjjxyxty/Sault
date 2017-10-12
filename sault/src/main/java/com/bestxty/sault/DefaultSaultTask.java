package com.bestxty.sault;

import android.net.Uri;

import com.bestxty.sault.Sault.Priority;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public class DefaultSaultTask implements SaultTask {

    private final int id;
    private final Sault sault;
    private final Object tag;
    private final String key;
    private final Uri uri;
    private final Callback callback;
    private final File target;
    private final Priority priority;
    private final boolean breakPointEnabled;

    private final Trace trace;
    private long totalSize;
    private AtomicLong finishedSize = new AtomicLong();

    public DefaultSaultTask(Sault sault, Object tag, Uri uri,
                            Callback callback, File target,
                            Priority priority, boolean breakPointEnabled) {
        this.sault = sault;
        this.tag = tag;
        this.uri = uri;
        this.callback = callback;
        this.target = target;
        this.priority = priority;
        this.breakPointEnabled = breakPointEnabled;
        this.id = Utils.generateTaskId();
        this.trace = new Trace();
        this.key = Utils.generateTaskKey(this);
    }

    @Override
    public Sault getSault() {
        return sault;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public File getTarget() {
        return target;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public Callback getCallback() {
        return callback;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public Progress getProgress() {
        return new Progress(totalSize, finishedSize.get());
    }

    @Override
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public void notifyFinishedSize(long stepSize) {
        finishedSize.addAndGet(stepSize);
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    @Override
    public Trace getTrace() {
        return trace;
    }

    @Override
    public void setStartTime(long startTime) {
        this.trace.setStartTime(startTime);
    }

    @Override
    public void setEndTime(long endTime) {
        this.trace.setEndTime(endTime);
    }


}
