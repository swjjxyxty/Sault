package com.bestxty.dl;

import android.net.Uri;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


import static com.bestxty.dl.Sault.Priority;

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

    TaskBuilder(Sault sault, Uri uri) {
        System.out.println("create task builder. uri=" + uri.toString());
        this.sault = sault;
        this.uri = uri;
    }

    public TaskBuilder priority(Priority priority) {
        System.out.println("set task priority. priority=" + priority.toString());
        this.priority = priority;
        return this;
    }

    public TaskBuilder tag(Object tag) {
        System.out.println("set task tag. tag=" + tag);
        this.tag = tag;
        return this;
    }

    public TaskBuilder listener(Callback callback) {
        System.out.println("set task listener");
        this.callback = callback;
        return this;
    }

    public TaskBuilder to(String file) {
        System.out.println("set task target file:" + file);
        return to(new File(file));
    }

    public TaskBuilder to(File file) {
        this.target = file;
        return this;
    }

    public Object go() {
        System.out.println("read to go task.");
        String key = createKey();
        if (tag == null) {
            System.out.println("not set tag, tag=key");
            tag = key;
        }
        if (target == null) {
            System.out.println("not set target , use default target file.");
            target = new File(sault.getSaveDir().getAbsolutePath() + File.separator + uri.getLastPathSegment());
        }
        if (priority == null) {
            System.out.println("not set priority, use default priority normal");
            priority = Priority.NORMAL;
        }

        Task task = new Task(sault, key, uri, target, tag, priority, callback);

        task.id = ID_GENERATOR.incrementAndGet();
        task.startTime = System.nanoTime();

        sault.enqueueAndSubmit(task);

        return task.getTag();
    }

    private String createKey() {
        return UUID.randomUUID().toString();
    }
}
