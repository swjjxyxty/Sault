package com.bestxty.sault;


import android.content.Context;
import android.net.Uri;
import android.os.Looper;

import com.bestxty.sault.dispatcher.AbstractCompositeEventDispatcher;
import com.bestxty.sault.dispatcher.CompositeEventDispatcher;
import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.handler.DefaultEventHandler;
import com.bestxty.sault.handler.MainThreadHandler;
import com.bestxty.sault.task.SaultTask;
import com.bestxty.sault.task.TaskBuilder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.bestxty.sault.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public final class Sault {

    private static SaultConfiguration DEFAULT_CONFIGURATION;

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
    private final AbstractCompositeEventDispatcher dispatcher;
    private final TaskRequestEventDispatcher taskRequestEventDispatcher;

    /**
     * file save dir.
     */
    private final File saveDir;

    /**
     * task map.
     */
    private final Map<Object, SaultTask> taskMap;

    private final String key;

    private volatile boolean loggingEnabled;

    private boolean breakPointEnabled;

    private boolean multiThreadEnabled;

    Sault(SaultConfiguration configuration, Context context) {
        this.saveDir = configuration.getSaveDir();
        this.loggingEnabled = configuration.isLoggingEnabled();
        this.breakPointEnabled = configuration.isBreakPointEnabled();
        this.multiThreadEnabled = configuration.isMultiThreadEnabled();
        this.key = configuration.getKey();
        taskMap = new LinkedHashMap<>();
        MainThreadHandler mainThreadHandler = new MainThreadHandler(Looper.getMainLooper());
        DefaultNetworkStatusProvider networkStatusProvider = new DefaultNetworkStatusProvider(context);

        ExecutorService executorService = configuration.getService();
        Downloader downloader = configuration.getDownloader();
        DefaultEventHandler defaultEventHandler
                = new DefaultEventHandler(executorService, downloader, networkStatusProvider);

        CompositeEventDispatcher compositeEventDispatcher
                = new CompositeEventDispatcher(mainThreadHandler);

        this.taskRequestEventDispatcher = compositeEventDispatcher;
        this.dispatcher = compositeEventDispatcher;

        defaultEventHandler.setHunterEventDispatcher(this.dispatcher);
        defaultEventHandler.setTaskEventDispatcher(this.dispatcher);
        defaultEventHandler.setTaskRequestEventDispatcher(this.dispatcher);

        compositeEventDispatcher.setTaskRequestEventHandler(defaultEventHandler);
        compositeEventDispatcher.setHunterEventHandler(defaultEventHandler);

        if (networkStatusProvider.accessNetwork()) {
            networkStatusProvider.register();
        }
    }

    String getKey() {
        return key;
    }

    public File getSaveDir() {
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
     * @param tag task's tag. {@link SaultTask#getTag()}
     */
    public void pause(Object tag) {
        SaultTask task = taskMap.get(tag);
        if (task != null) {
            dispatcher.dispatchSaultTaskPauseRequest(task);
        }
    }


    /**
     * resume task by tag.
     *
     * @param tag task's tag. {@link SaultTask#getTag()}
     */
    public void resume(Object tag) {
        SaultTask task = taskMap.get(tag);
        if (task != null) {
            dispatcher.dispatchSaultTaskResumeRequest(task);
        }
    }


    /**
     * cancel task by tag.
     */
    public void cancel(Object tag) {
        SaultTask task = taskMap.get(tag);
        if (task != null) {
            dispatcher.dispatchSaultTaskCancelRequest(task);
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
    }


    public void enqueueAndSubmit(SaultTask task) {
        SaultTask source = taskMap.get(task.getTag());
        if (source == null) {
            taskMap.put(task.getTag(), task);
        }
        submit(task);
    }


    private Stats dump() {
//        Stats stats = dispatcher.getStats();
//        stats.taskSize = taskMap.size();
//        return stats;
        return null;
    }

    private void cancelTask(SaultTask task) {
        taskMap.remove(task.getTag());
    }

    private void resumeTask(SaultTask task) {
        Callback callback = task.getCallback();
        if (callback != null) {
            callback.onEvent(task.getTag(), Callback.EVENT_RESUME);
        }
        enqueueAndSubmit(task);
    }

//    private void complete(TaskHunter hunter) {
//        Task single = hunter.getTask();
//        taskMap.remove(single.getTag());
//        Callback callback = single.getCallback();
//        if (callback != null) {
//            callback.onEvent(single.getTag(), Callback.EVENT_COMPLETE);
//            callback.onComplete(single.getTag(), single.getTarget().getAbsolutePath());
//        }
//    }

    private void submit(SaultTask task) {
        if (isLoggingEnabled())
            log("submit task. task=" + task.getKey());
        taskRequestEventDispatcher.dispatchSaultTaskSubmitRequest(task);
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
