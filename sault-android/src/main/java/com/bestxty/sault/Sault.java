package com.bestxty.sault;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bestxty.sault.Dispatcher.HUNTER_BATCH_CANCEL;
import static com.bestxty.sault.Dispatcher.HUNTER_BATCH_COMPLETE;
import static com.bestxty.sault.Dispatcher.HUNTER_NOTIFY;
import static com.bestxty.sault.Dispatcher.TASK_BATCH_RESUME;
import static com.bestxty.sault.Utils.Informer;
import static com.bestxty.sault.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public final class Sault {

    private static SaultConfiguration DEFAULT_CONFIGURATION;


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
                    batch.clear();
                    break;
                }
                case HUNTER_BATCH_COMPLETE: {
                    @SuppressWarnings("unchecked") List<TaskHunter> batch = (List<TaskHunter>) msg.obj;
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        TaskHunter hunter = batch.get(i);
                        hunter.getSault().complete(hunter);
                    }
                    batch.clear();
                    break;
                }
                case HUNTER_BATCH_CANCEL: {
                    @SuppressWarnings("unchecked") List<Task> batch = (List<Task>) msg.obj;
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        Task task = batch.get(i);
                        task.getSault().cancelTask(task);
                    }
                    batch.clear();
                    break;
                }
                case HUNTER_NOTIFY: {
                    Informer informer = (Informer) msg.obj;
                    informer.callNotify();
                    break;
                }
            }
        }
    };

    public static int calculateProgress(long finishedSize, long totalSize) {
        if (totalSize == 0) throw new IllegalArgumentException("total size must great than zero!");
        return (int) (finishedSize * 100 / totalSize);
    }

    public static void setDefaultConfiguration(SaultConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("A non-null SaultConfiguration must be provided");
        }
        DEFAULT_CONFIGURATION = configuration;
    }

    public static Sault getInstance(SaultConfiguration configuration, Context context) {
        if (configuration == null) {
            throw new NullPointerException("A non-null SaultConfiguration must be provided");
        }
        return SaultCache.createSaultOrGetFromCache(configuration, context);
    }


    public static Sault getInstance(Context context) {
        if (DEFAULT_CONFIGURATION == null) {
            throw new NullPointerException("No default SaultConfiguration was found. Call setDefaultConfiguration() first.");
        }
        return SaultCache.createSaultOrGetFromCache(DEFAULT_CONFIGURATION, context);
    }

    /**
     * task priority.
     * default value is normal.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }

    /**
     * task dispatcher.
     */
    private final Dispatcher dispatcher;

    /**
     * file save dir.
     */
    private final File saveDir;

    /**
     * task map.
     */
    private final Map<Object, Task> taskMap;

    private final String key;

    private volatile boolean loggingEnabled;

    private boolean breakPointEnabled;

    private boolean multiThreadEnabled;

    Sault(SaultConfiguration configuration, Context context) {
        this.dispatcher = new Dispatcher(context.getApplicationContext(), configuration.getService(),
                HANDLER, configuration.getDownloader(), configuration.isAutoAdjustThreadEnabled());
        this.saveDir = configuration.getSaveDir();
        this.loggingEnabled = configuration.isLoggingEnabled();
        this.breakPointEnabled = configuration.isBreakPointEnabled();
        this.multiThreadEnabled = configuration.isMultiThreadEnabled();
        this.key = configuration.getKey();
        taskMap = new LinkedHashMap<>();
    }

    String getKey() {
        return key;
    }

    File getSaveDir() {
        return saveDir;
    }

    public Stats getStats() {
        return dump();
    }


    /**
     * {@code true} if debug logging is enabled.
     *
     * @return loggingEnable
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isMultiThreadEnabled() {
        return multiThreadEnabled;
    }


    public TaskBuilder load(String url) {
        return new TaskBuilder(this, Uri.parse(url));
    }

    /**
     * pause task by tag.
     *
     * @param tag task's tag. {@link Task#getTag()}
     */
    public void pause(Object tag) {
        dispatcher.dispatchPauseTag(tag);
    }


    /**
     * resume task by tag.
     *
     * @param tag task's tag. {@link Task#getTag()}
     */
    public void resume(Object tag) {
        dispatcher.dispatchResumeTag(tag);
    }


    /**
     * cancel task by tag.
     *
     * @param tag task's tag. {@link Task#getTag()}
     */
    public void cancel(Object tag) {
        Task task = taskMap.get(tag);
        if (task != null) {
            dispatcher.dispatchCancel(task);
        } else {
            if (isLoggingEnabled())
                log("cancel failed. tag not exist!");
        }
    }

    public void close() {
        SaultCache.release(this);
    }

    /**
     * shutdown .
     * release resources.
     */
    void shutdown() {
        dispatcher.shutdown();
        HANDLER.removeCallbacksAndMessages(null);
    }


    void enqueueAndSubmit(Task task) {
        Task source = taskMap.get(task.getTag());
        if (source == null) {
            taskMap.put(task.getTag(), task);
        }
        submit(task);
    }


    private Stats dump() {
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
        Task single = hunter.getTask();
        taskMap.remove(single.getTag());
        Callback callback = single.getCallback();
        if (callback != null) {
            callback.onEvent(single.getTag(), Callback.EVENT_COMPLETE);
            callback.onComplete(single.getTag(), single.getTarget().getAbsolutePath());
        }
    }

    private void submit(Task task) {
        if (isLoggingEnabled())
            log("submit task. task=" + task.getKey());
        dispatcher.dispatchSubmit(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sault sault = (Sault) o;

        return key != null ? key.equals(sault.key) : sault.key == null;

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
