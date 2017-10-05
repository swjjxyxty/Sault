package com.bestxty.sault;

import android.net.Uri;

import java.io.File;
import java.util.UUID;

import static com.bestxty.sault.Sault.Priority;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public class TaskBuilder {

    private static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
    private static final int KEY_PADDING = 50; // Determined by exact science.
    private static final char KEY_SEPARATOR = '\n';


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

        String key = createKey();
        if (tag == null) {
            tag = UUID.randomUUID().toString();
        }

        Task task = new Task(sault, key, uri, target, tag, priority, callback,
                multiThreadEnabled, breakPointEnabled);

        task.startTime = System.nanoTime();

        sault.enqueueAndSubmit(task);

        return task.getTag();
    }


    private String createKey() {
        String key = createKey(MAIN_THREAD_KEY_BUILDER);

        MAIN_THREAD_KEY_BUILDER.setLength(0);

        return key;
    }

    private String createKey(StringBuilder builder) {
        String path = uri.toString();
        builder.ensureCapacity(path.length() + KEY_PADDING);
        builder.append(path);

        builder.append(KEY_SEPARATOR);
        builder.append("target:").append(target.getPath());

        builder.append(KEY_SEPARATOR);
        builder.append("tag:").append(tag);

        builder.append(KEY_SEPARATOR);
        builder.append("priority:").append(priority.name());

        builder.append(KEY_SEPARATOR);
        builder.append("multiThreadEnable:").append(multiThreadEnabled);

        builder.append(KEY_SEPARATOR);
        builder.append("breakPointEnable:").append(breakPointEnabled);

        builder.append(KEY_SEPARATOR);
        builder.append("hasCallback:").append(callback != null);


        return builder.toString();
    }


}
