package com.bestxty.dl;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import static com.bestxty.dl.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public final class Sault {


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


    private boolean loggingEnabled;

    private boolean breakPointEnabled;
    private boolean multiThreadEnabled;

    Sault(Dispatcher dispatcher, File saveDir, boolean loggingEnabled) {
        this.dispatcher = dispatcher;
        this.saveDir = saveDir;
        this.loggingEnabled = loggingEnabled;
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


    /**
     * {@code true} if debug logging is enabled.
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    public boolean isMultiThreadEnabled() {
        return multiThreadEnabled;
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
        log("complete() called with: hunter = [" + hunter + "]");
        Task single = hunter.getTask();
        taskMap.remove(single.getTag());
        Callback callback = single.getCallback();
        if (callback != null) {
            callback.onEvent(single.getTag(), Callback.EVENT_COMPLETE);
            callback.onComplete(single.getTag(), single.getTarget().getAbsolutePath());
        }
    }

    void enqueueAndSubmit(Task task) {
        log("enqueue and submit task.task=" + task.getKey());
        Task source = taskMap.get(task.getTag());

        if (source == null) {
            taskMap.put(task.getTag(), task);
            log("put task to task map");
        }
        submit(task);
    }

    private void submit(Task task) {
        log("submit task. task=" + task.getKey());
        dispatcher.dispatchSubmit(task);
    }

    public TaskBuilder load(String url) {
        log("load task from url:" + url);
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
            log("not found need cancel task.");
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
        private boolean loggingEnabled = false;
        private boolean breakPointEnabled = true;
        private boolean multiThreadEnabled = true;
        private boolean autoAdjustThreadEnabled = true;

        private int threadCount = -1;
        private int networkWIFIThreadCount = -1;  //wifi
        private int networkLTEThreadCount = -1;     //4G
        private int networkCDMAThreadCount = -1;    //3G
        private int networkGPRSThreadCount = -1;    //2G

        public Builder(Context context) {
            log("create sault builder.");
            this.context = context;
        }

        public Builder saveDir(String saveDir) {
            log("set default file save dir. saveDir=" + saveDir);
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

        public Builder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder networkWIFIThreadCount(int networkWIFIThreadCount) {
            this.networkWIFIThreadCount = networkWIFIThreadCount;
            return this;
        }

        public Builder network4GThreadCount(int networkLTEThreadCount) {
            this.networkLTEThreadCount = networkLTEThreadCount;
            return this;
        }

        public Builder network3GThreadCount(int networkCDMAThreadCount) {
            this.networkCDMAThreadCount = networkCDMAThreadCount;
            return this;
        }

        public Builder network2GThreadCount(int networkGPRSThreadCount) {
            this.networkGPRSThreadCount = networkGPRSThreadCount;
            return this;
        }

        /**
         * Toggle whether debug logging is enabled.
         * <p>
         * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
         * be used for debugging purposes. Do NOT pass {@code BuildConfig.DEBUG}.
         */
        public Builder loggingEnabled(boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        public Builder breakPointEnabled(boolean enabled) {
            this.breakPointEnabled = enabled;
            return this;
        }

        public Builder multiThreadEnabled(boolean enabled) {
            this.multiThreadEnabled = enabled;
            return this;
        }

        public Builder autoAdjustThreadEnabled(boolean enabled) {
            this.autoAdjustThreadEnabled = enabled;
            return this;
        }


        private void setupSaultExecutorService() {
            SaultExecutorService executorService = (SaultExecutorService) service;
            if (threadCount != -1) {
                executorService.setThreadCount(threadCount);
            }
            if (networkWIFIThreadCount != -1) {
                executorService.setNetworkWIFIThreadCount(networkWIFIThreadCount);
            }
            if (networkLTEThreadCount != -1) {
                executorService.setNetworkLTEThreadCount(networkLTEThreadCount);
            }
            if (networkCDMAThreadCount != -1) {
                executorService.setNetworkCDMAThreadCount(networkCDMAThreadCount);
            }
            if (networkGPRSThreadCount != -1) {
                executorService.setNetworkGPRSThreadCount(networkGPRSThreadCount);
            }
            log(String.format(Locale.CHINA, "set up sault executor service. " +
                            "threadCount=%d,wifiThreadCount=%d,4GThreadCount=%d,3GThreadCount=%d,2GThreadCount=%d.",
                    threadCount, networkWIFIThreadCount, networkLTEThreadCount,
                    networkCDMAThreadCount, networkGPRSThreadCount));
        }

        public Sault build() {
            log("build sault.");
            if (service == null) {
                log("not set executor service ,create default sault executor service.");
                service = new SaultExecutorService();
                setupSaultExecutorService();
            }
            if (downloader == null) {
                log("not set downloader ,create default okhttp downloader.");
                downloader = new OkHttpDownloader();
            }
            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downloader,
                    autoAdjustThreadEnabled);
            return new Sault(dispatcher, saveDir, loggingEnabled);
        }
    }
}
