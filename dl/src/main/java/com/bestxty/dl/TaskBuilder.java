package com.bestxty.dl;

import android.net.Uri;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


import static com.bestxty.dl.Sault.Priority;
import static com.bestxty.dl.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public class TaskBuilder {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private Uri uri;
    private Sault sault;
    private Priority priority;
    private Object tag;
    private Callback callback;
    private File target;
    private Boolean multiThreadEnabled = null;
    private Boolean breakPointEnabled = null;

    TaskBuilder(Sault sault, Uri uri) {
        log("create task builder. uri=" + uri.toString());
        this.sault = sault;
        this.uri = uri;
    }


    public TaskBuilder breakPointEnabled(boolean breakPointEnabled) {
        this.breakPointEnabled = breakPointEnabled;
        return this;
    }


    public TaskBuilder multiThreadEnabled(boolean multiThreadEnabled) {
        this.multiThreadEnabled = multiThreadEnabled;
        return this;
    }


    public TaskBuilder priority(Priority priority) {
        log("set task priority. priority=" + priority.toString());
        this.priority = priority;
        return this;
    }

    public TaskBuilder tag(Object tag) {
        log("set task tag. tag=" + tag);
        this.tag = tag;
        return this;
    }

    public TaskBuilder listener(Callback callback) {
        log("set task listener");
        this.callback = callback;
        return this;
    }

    public TaskBuilder to(String file) {
        log("set task target file:" + file);
        return to(new File(file));
    }

    public TaskBuilder to(File file) {
        this.target = file;
        return this;
    }

    public Object go() {
        log("read to go task.");
        String key = createKey();
        if (tag == null) {
            log("not set tag, tag=key");
            tag = key;
        }
        if (target == null) {
            log("not set target , use default target file.");
            target = new File(sault.getSaveDir().getAbsolutePath() + File.separator + uri.getLastPathSegment());
        }
        if (priority == null) {
            log("not set priority, use default priority normal");
            priority = Priority.NORMAL;
        }

        if (breakPointEnabled == null) {
            breakPointEnabled = sault.isBreakPointEnabled();
        }

        if (multiThreadEnabled == null) {
            multiThreadEnabled = sault.isMultiThreadEnabled();
        }


        Task task = new Task(sault, key, uri, target, tag, priority, callback,
                multiThreadEnabled, breakPointEnabled);

        task.id = ID_GENERATOR.incrementAndGet();
        task.startTime = System.nanoTime();

        sault.enqueueAndSubmit(task);

        return task.getTag();
    }

    private String createKey() {
        return UUID.randomUUID().toString();
    }
}
