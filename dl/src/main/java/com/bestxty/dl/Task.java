package com.bestxty.dl;

import android.net.Uri;

import java.io.File;

import static com.bestxty.dl.Sault.Priority;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
final class Task {
    int id;
    long startTime;
    long endTime;
    private final Object tag;
    private final File target;
    private final Callback callback;
    private final Priority priority;
    private final Uri uri;
    private final String key;
    private final Sault sault;

    public Task(Sault sault, String key, Uri uri, File target, Object tag, Priority priority,
                Callback callback) {
        this.sault = sault;
        this.key = key;
        this.uri = uri;
        this.target = target;
        this.tag = tag;
        this.priority = priority;
        this.callback = callback;
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
}
