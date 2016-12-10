package com.bestxty.dl;


import android.content.Context;
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

import static com.bestxty.dl.Callback.EVENT_CANCEL;
import static com.bestxty.dl.Dispatcher.HUNTER_BATCH_COMPLETE;
import static com.bestxty.dl.Dispatcher.HUNTER_ERROR;
import static com.bestxty.dl.Dispatcher.HUNTER_PROGRESS;
import static com.bestxty.dl.Dispatcher.TASK_BATCH_RESUME;
import static com.bestxty.dl.Dispatcher.TASK_EVENT;
import static com.bestxty.dl.Utils.ErrorInformer;
import static com.bestxty.dl.Utils.EventInformer;
import static com.bestxty.dl.Utils.ProgressInformer;

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
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        TaskHunter hunter = batch.get(i);
                        hunter.getSault().complete(hunter);
                    }
                    break;
                }
                case HUNTER_PROGRESS: {
                    ProgressInformer informer = (ProgressInformer) msg.obj;
                    informer.notifyProgress();
                    break;
                }
                case TASK_EVENT: {
                    EventInformer informer = (EventInformer) msg.obj;
                    if (informer.event == EVENT_CANCEL) {
                        Task task = informer.task;
                        task.getSault().cancelTask(task);
                    }
                    informer.notifyEvent();
                    break;
                }
                case HUNTER_ERROR: {
                    ErrorInformer informer = (ErrorInformer) msg.obj;
                    informer.notifyError();
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

    public Stats getStats() {
        Stats stats = dispatcher.getStats();
        stats.taskSize = taskMap.size();
        return stats;
    }

    private void cancelTask(Task task) {
        taskMap.remove(task.getTag());
    }

    private void resumeTask(Task task) {
        Callback callback = task.getCallback();
        if (callback != null) {
            callback.onEvent(task.getTag(), Callback.EVENT_RESUME);
        }
        enqueueAndSubmit(task);
    }

    private void complete(TaskHunter hunter) {
        Log.d(TAG, "complete() called with: hunter = [" + hunter + "]");
        Task single = hunter.getTask();
        taskMap.remove(single.getTag());
        Callback callback = single.getCallback();
        if (callback != null) {
            callback.onEvent(single.getTag(), Callback.EVENT_COMPLETE);
            callback.onComplete(single.getTag(), single.getTarget().getAbsolutePath());
        }
    }

    void enqueueAndSubmit(Task task) {
        System.out.println("enqueue and submit task.task=" + task.getKey());
        Task source = taskMap.get(task.getTag());

        if (source == null) {
            taskMap.put(task.getTag(), task);
            System.out.println("put task to task map");
        }
        submit(task);
    }

    void submit(Task task) {
        System.out.println("submit task. task=" + task.getKey());
        dispatcher.dispatchSubmit(task);
    }

    public TaskBuilder load(String url) {
        System.out.println("load task from url:" + url);
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
        if (task != null) {
            dispatcher.dispatchCancel(task);
        } else {
            System.out.println("not found need cancel task.");
        }
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
            System.out.println("create sault builder.");
            this.context = context;
        }

        public Builder saveDir(String saveDir) {
            System.out.println("set default file save dir. saveDir=" + saveDir);
            return saveDir(new File(saveDir));
        }

        public Builder saveDir(File saveDir) {
            this.saveDir = saveDir;
            return this;
        }

        public Builder downloader(Downloader downloader) {
            this.downloader = downloader;
            return this;
        }

        public Builder executor(ExecutorService service) {
            this.service = service;
            return this;
        }

        public Sault build() {
            System.out.println("build sault");
            if (service == null) {
                System.out.println("not set executor service ,create default sault executor service.");
                service = new SaultExecutorService();
            }
            if (downloader == null) {
                System.out.println("not set downloader ,create default okhttp downloader.");
                downloader = new OkHttpDownloader();
            }
            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downloader);
            return new Sault(dispatcher, saveDir);
        }
    }
}
