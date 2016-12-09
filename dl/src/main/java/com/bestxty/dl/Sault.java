package com.bestxty.dl;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.bestxty.dl.Dispatcher.HUNTER_BATCH_COMPLETE;
import static com.bestxty.dl.Dispatcher.TASK_BATCH_RESUME;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public final class Sault {

    private static final String TAG = "Sault";


    static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_BATCH_RESUME: {
                    @SuppressWarnings("unchecked") List<Task> batch = (List<Task>) msg.obj;
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        Task task = batch.get(i);
                        task.getSault().resumeTask(task);
                    }
                    break;
                }
                case HUNTER_BATCH_COMPLETE: {
                    @SuppressWarnings("unchecked") List<TaskHunter> batch = (List<TaskHunter>) msg.obj;
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        TaskHunter hunter = batch.get(i);
                        hunter.getSault().complete(hunter);
                    }
                    break;
                }
            }
        }
    };

    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }

    private final Dispatcher dispatcher;

    private final File saveDir;

    private final Map<Object, Task> taskMap;

    Sault(Dispatcher dispatcher, File saveDir) {
        this.dispatcher = dispatcher;
        this.saveDir = saveDir;
        taskMap = new LinkedHashMap<>();
    }

    File getSaveDir() {
        return saveDir;
    }

    private void resumeTask(Task task) {
        submit(task);
    }

    private void complete(TaskHunter hunter) {
        Log.d(TAG, "complete() called with: hunter = [" + hunter + "]");
        Task single = hunter.getTask();
        taskMap.remove(single.getTag());
    }

    void enqueueAndSubmit(Task task) {
        Task source = taskMap.get(task.getTag());

        if (source != null && source != task) {
            // TODO: 2016/12/9 cancel already task
            taskMap.put(task.getTag(), task);
        }
        submit(task);
    }

    void submit(Task task) {
        dispatcher.dispatchSubmit(task);
    }

    public TaskBuilder load(String url) {
        return new TaskBuilder(this, Uri.parse(url));
    }

    public void pause(Object tag) {
        dispatcher.dispatchPauseTag(tag);
    }

    public void resume(Object tag) {
        dispatcher.dispatchResumeTag(tag);
    }

    public void cancel(Object tag) {
        Task task = taskMap.get(tag);
        if (task != null)
            dispatcher.dispatchCancel(task);
    }

    public void shutdown() {
        dispatcher.shutdown();
    }

    public static class Builder {

        private File saveDir;
        private ExecutorService service;
        private Downloader downloader;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder saveDir(String saveDir) {
            return saveDir(new File(saveDir));
        }

        public Builder saveDir(File saveDir) {
            this.saveDir = saveDir;
            return this;
        }

        public Sault build() {
            if (service == null) {
                service = new SaultExecutorService();
            }
            if (downloader == null) {
                downloader = new OkHttpDownloader();
            }
            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downloader);
            return new Sault(dispatcher, saveDir);
        }
    }
}
