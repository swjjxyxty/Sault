package com.bestxty.sault;

import android.net.Uri;

import java.io.File;

import static com.bestxty.sault.Sault.Priority;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public class TaskBuilder {


    private Uri uri;
    private Sault sault;
    private Priority priority;
    private Object tag;
    private Callback callback;
    private File target;
    private Boolean multiThreadEnabled = null;
    private Boolean breakPointEnabled = null;

    TaskBuilder(Sault sault, Uri uri) {
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
        this.priority = priority;
        return this;
    }

    public TaskBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public TaskBuilder listener(Callback callback) {
        this.callback = callback;
        return this;
    }

    public TaskBuilder to(String file) {
        return to(new File(file));
    }

    public TaskBuilder to(File file) {
        this.target = file;
        return this;
    }

    public Object go() {

        if (target == null) {
            target = new File(sault.getSaveDir().getAbsolutePath() + File.separator + uri.getLastPathSegment());
        }
        if (priority == null) {
            priority = Priority.NORMAL;
        }

        if (breakPointEnabled == null) {
            breakPointEnabled = sault.isBreakPointEnabled();
        }

        if (multiThreadEnabled == null) {
            multiThreadEnabled = sault.isMultiThreadEnabled();
        }

        Task task = new Task(sault, uri, target, tag, priority, callback,
                multiThreadEnabled, breakPointEnabled);

        sault.enqueueAndSubmit(task);

        return task.getTag();
    }


}
