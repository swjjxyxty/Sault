package com.bestxty.sault;


import android.content.Context;
import android.net.Uri;

import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.internal.di.components.DaggerSaultComponent;
import com.bestxty.sault.internal.di.components.SaultComponent;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.task.SaultTask;
import com.bestxty.sault.task.TaskBuilder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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


    public static Sault getInstance(Context context) {
        if (DEFAULT_CONFIGURATION == null) {
            throw new NullPointerException("No default SaultConfiguration was found. Call setDefaultConfiguration() first.");
        }
        Sault sault = SaultHolder.INSTANCE.getSault();
        if (sault == null) {
            return SaultHolder.INSTANCE.newSault(DEFAULT_CONFIGURATION, context);
        }
        return sault;
    }

    private enum SaultHolder {
        INSTANCE;
        private Sault sault;

        public Sault newSault(SaultConfiguration configuration, Context context) {
            this.sault = new Sault(configuration, context);
            return this.sault;
        }

        public Sault getSault() {
            return sault;
        }
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
    @Inject
    TaskRequestEventDispatcher taskRequestEventDispatcher;

    /**
     * file save dir.
     */
    @Inject
    File saveDir;

    /**
     * task map.
     */
    private final Map<Object, SaultTask> taskMap;

    @Inject
    @Named("saultKey")
    String key;

    @Inject
    @Named("loggingEnabled")
    volatile boolean loggingEnabled;

    @Inject
    @Named("breakPointEnabled")
    boolean breakPointEnabled;

    @Inject
    @Named("multiThreadEnabled")
    boolean multiThreadEnabled;

    @Inject
    NetworkStatusProvider networkStatusProvider;

    private SaultComponent saultComponent;

    public SaultComponent getSaultComponent() {
        return saultComponent;
    }

    Sault(SaultConfiguration configuration, Context context) {
        saultComponent = DaggerSaultComponent.builder()
                .saultModule(new SaultModule(context, configuration))
                .build();
        saultComponent.inject(this);

        taskMap = new LinkedHashMap<>();
        if (networkStatusProvider.accessNetwork()) {
            ((DefaultNetworkStatusProvider) networkStatusProvider).register();
        }
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
            taskRequestEventDispatcher.dispatchSaultTaskPauseRequest(task);
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
            taskRequestEventDispatcher.dispatchSaultTaskResumeRequest(task);
        }
    }


    /**
     * cancel task by tag.
     */
    public void cancel(Object tag) {
        SaultTask task = taskMap.get(tag);
        if (task != null) {
            taskRequestEventDispatcher.dispatchSaultTaskCancelRequest(task);
        }
    }

    public void close() {
        shutdown();
    }

    /**
     * shutdown .
     * release resources.
     */
    void shutdown() {
        taskRequestEventDispatcher.shutdown();
        ((DefaultNetworkStatusProvider) networkStatusProvider).unregister();
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
