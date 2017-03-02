package com.bestxty.sault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.bestxty.sault.Utils.Informer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.bestxty.sault.Callback.EVENT_CANCEL;
import static com.bestxty.sault.Callback.EVENT_PAUSE;
import static com.bestxty.sault.Callback.EVENT_START;
import static com.bestxty.sault.Utils.DISPATCHER_THREAD_NAME;
import static com.bestxty.sault.Utils.ErrorInformer;
import static com.bestxty.sault.Utils.EventInformer;
import static com.bestxty.sault.Utils.ProgressInformer;
import static com.bestxty.sault.Utils.THREAD_PREFIX;
import static com.bestxty.sault.Utils.getService;
import static com.bestxty.sault.Utils.hasPermission;
import static com.bestxty.sault.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
@SuppressWarnings("WeakerAccess")
class Dispatcher {

    static final int TASK_SUBMIT = 1;
    static final int TASK_PAUSE = 2;
    static final int TASK_RESUME = 3;
    static final int TASK_CANCEL = 4;

    static final int HUNTER_COMPLETE = 5;
    static final int HUNTER_FAILED = 6;
    static final int HUNTER_RETRY = 7;

    static final int TASK_BATCH_RESUME = 8;

    static final int HUNTER_DELAY_NEXT_BATCH = 9;
    static final int HUNTER_BATCH_COMPLETE = 10;
    static final int HUNTER_BATCH_CANCEL = 11;
    static final int HUNTER_NOTIFY = 12;

    static final int NETWORK_STATE_CHANGE = 13;
    static final int AIRPLANE_MODE_CHANGE = 14;

    private static final int BATCH_DELAY = 200; // ms
    private static final int RETRY_DELAY = 500;


    private static final int AIRPLANE_MODE_ON = 1;
    private static final int AIRPLANE_MODE_OFF = 0;

    private final ExecutorService service;
    private final DispatcherThread dispatcherThread;
    private final Handler handler;
    private final Handler mainThreadHandler;
    private final Map<String, TaskHunter> hunterMap;
    private final Map<String, Task> pausedTaskMap;
    private final Map<String, Task> failedTaskMap;
    private final Downloader downloader;
    private final Set<Object> pausedTags;
    private final List<TaskHunter> batchComplete;
    private final List<Task> batchCancel;

    private final NetworkBroadcastReceiver receiver;
    private final boolean scansNetworkChanges;
    private final Context context;
    private boolean airplaneMode;

    private boolean autoAdjustThreadEnabled;

    Dispatcher(Context context,
               ExecutorService service,
               Handler mainThreadHandler,
               Downloader downloader,
               boolean autoAdjustThreadEnabled) {
        this.context = context;
        this.service = service;
        this.mainThreadHandler = mainThreadHandler;
        this.downloader = downloader;
        this.autoAdjustThreadEnabled = autoAdjustThreadEnabled;
        this.dispatcherThread = new DispatcherThread();
        dispatcherThread.start();
        this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);
        this.hunterMap = new LinkedHashMap<>();
        this.pausedTaskMap = new HashMap<>();
        this.failedTaskMap = new HashMap<>();
        this.pausedTags = new HashSet<>();
        this.batchComplete = new ArrayList<>(4);
        this.batchCancel = new ArrayList<>(4);

        this.airplaneMode = Utils.isAirplaneModeOn(this.context);
        this.scansNetworkChanges = hasPermission(context, ACCESS_NETWORK_STATE);
        this.receiver = new NetworkBroadcastReceiver(this);
        receiver.register();
    }

    boolean isAutoAdjustThreadEnabled() {
        return autoAdjustThreadEnabled;
    }

    void shutdown() {
        // Shutdown the thread pool only if it is the one created by Sault.
        if (service instanceof SaultExecutorService) {
            service.shutdown();
        }

        handler.removeCallbacksAndMessages(null);

        dispatcherThread.quit();
//         Unregister network broadcast receiver on the main thread.
        Sault.HANDLER.post(new Runnable() {
            @Override
            public void run() {
                receiver.unregister();
            }
        });
    }


    Stats getStats() {
        Stats stats = new Stats();
        stats.hunterMapSize = hunterMap.size();
        stats.batchSize = batchComplete.size();
        stats.failedTaskSize = failedTaskMap.size();
        stats.pausedTagSize = pausedTags.size();
        stats.pausedTaskSize = pausedTaskMap.size();
        if (service instanceof SaultExecutorService) {

            SaultExecutorService executorService = (SaultExecutorService) service;
            stats.activeCount = executorService.getActiveCount();
            stats.corePoolSize = executorService.getCorePoolSize();
            stats.largestPoolSize = executorService.getLargestPoolSize();
            stats.maximumPoolSize = executorService.getMaximumPoolSize();
            stats.poolSize = executorService.getPoolSize();

            stats.taskCount = executorService.getTaskCount();
            stats.completeTaskCount = executorService.getCompletedTaskCount();
        }

        return stats;
    }

    Future submit(Runnable runnable) {
        return service.submit(runnable);
    }


    private void dispatchInformer(Informer informer) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_NOTIFY, informer));
    }

    private void dispatchError(ErrorInformer errorInformer) {
        dispatchInformer(errorInformer);
    }

    private void dispatchEvent(EventInformer eventInformer) {
        dispatchInformer(eventInformer);
    }

    void dispatchProgress(ProgressInformer progressInformer) {
        dispatchInformer(ProgressInformer.create(progressInformer));
    }

    void dispatchSubmit(Task task) {
        handler.sendMessage(handler.obtainMessage(TASK_SUBMIT, task));
    }

    void dispatchPauseTag(Object tag) {
        log(String.format(Locale.CHINA, "dispatch pause. paused size=%d,paused tag=%d,failed size=%d,hunter size=%d.",
                pausedTaskMap.size(),
                pausedTags.size(),
                failedTaskMap.size(),
                hunterMap.size()));
        handler.sendMessage(handler.obtainMessage(TASK_PAUSE, tag));
    }

    void dispatchCancel(Task task) {
        log("dispatch cancel.");
        handler.sendMessage(handler.obtainMessage(TASK_CANCEL, task));
    }

    void dispatchResumeTag(Object tag) {
        handler.sendMessage(handler.obtainMessage(TASK_RESUME, tag));
    }

    void dispatchComplete(TaskHunter hunter) {
        handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    void dispatchFailed(TaskHunter hunter) {
        log("dispatch task failed. ex=" + hunter.getException().getMessage());
        hunter.getException().printStackTrace();
        handler.sendMessage(handler.obtainMessage(HUNTER_FAILED, hunter));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    void dispatchRetry(TaskHunter hunter) {
        log("dispatch task retry.ex=" + hunter.getException().getMessage());
        hunter.getException().printStackTrace();
        handler.sendMessageDelayed(handler.obtainMessage(HUNTER_RETRY, hunter), RETRY_DELAY);
    }

    private void dispatchNetworkStateChange(NetworkInfo info) {
        handler.sendMessage(handler.obtainMessage(NETWORK_STATE_CHANGE, info));
    }

    private void dispatchAirplaneModeChange(boolean airplaneMode) {
        handler.sendMessage(handler.obtainMessage(AIRPLANE_MODE_CHANGE,
                airplaneMode ? AIRPLANE_MODE_ON : AIRPLANE_MODE_OFF, 0));
    }


    private void performSubmit(Task task) {
        log("perform submit task,task=" + task.getKey());
        if (!task.isMultiThreadEnabled()) {
            TaskHunter hunter = new SaultDefaultTaskHunter(task.getSault(),
                    this, task, downloader);
            Future<?> future = service.submit(hunter);
            hunter.setFuture(future);
            hunterMap.put(hunter.getKey(), hunter);
            log("put hunter to hunter map. size=" + hunterMap.size());
            dispatchEvent(EventInformer.create(task, EVENT_START));
            return;
        }

        TaskHunter hunter = new SaultMultiPartTaskHunter(task.getSault(), this, task, downloader);

        Future<?> future = service.submit(hunter);
        hunter.setFuture(future);
        hunterMap.put(hunter.getKey(), hunter);
        log("put hunter to hunter map. size=" + hunterMap.size());
        dispatchEvent(EventInformer.create(task, EVENT_START));

    }

    private void performPause(Object tag) {
        if (!pausedTags.add(tag)) {
            log("tag is already in paused tag set.");
            return;
        }

        log("ready pause task for tag:" + tag);
        for (Iterator<TaskHunter> iterator = hunterMap.values().iterator(); iterator.hasNext(); ) {
            TaskHunter hunter = iterator.next();
            Task single = hunter.getTask();
            if (single == null) {
                continue;
            }

            if (single.getTag().equals(tag)) {
                log("find task");
                log("detach task for hunter");
                if (hunter.cancel()) {
                    log("cancel hunter");
                    iterator.remove();
                    log("put task to paused task map");
                    pausedTaskMap.put(single.getKey(), single);
                    dispatchEvent(EventInformer.create(single, EVENT_PAUSE));
                }
            }
        }
        log("perform pause finish");
    }

    private void performResume(Object tag) {
        if (!pausedTags.remove(tag)) {
            log("paused tag set not contain tag.");
            return;
        }
        log("ready resume task for tag:" + tag);
        List<Task> batch = null;
        for (Iterator<Task> iterator = pausedTaskMap.values().iterator(); iterator.hasNext(); ) {
            Task task = iterator.next();
            if (task.getTag().equals(tag)) {
                if (batch == null) {
                    batch = new ArrayList<>();
                }
                batch.add(task);
                iterator.remove();
            }
        }

        if (batch != null) {
            log("find need resumed task size=" + batch.size());
            mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TASK_BATCH_RESUME, batch));
        } else {
            log("not found need resumed task");
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void performCancel(Task task) {
        log("perform cancel.");
        String key = task.getKey();
        TaskHunter hunter = hunterMap.get(key);
        if (hunter != null) {
            log("find hunter");
            if (hunter.cancel()) {
                log("cancel hunter");
                dispatchEvent(EventInformer.create(task, EVENT_CANCEL));
                batchCancel(task);
                hunterMap.remove(key);
            }
        }

        if (pausedTags.contains(task.getTag())) {
            log("paused tags contain task");
            pausedTaskMap.remove(key);
            pausedTags.remove(task.getTag());
            dispatchEvent(EventInformer.create(task, EVENT_CANCEL));
            batchCancel(task);
        }

        Task remove = failedTaskMap.remove(key);
        if (remove != null) {
            log("task removed create failed task map");
            batchCancel(task);
            dispatchEvent(EventInformer.create(task, EVENT_CANCEL));
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void performComplete(TaskHunter hunter) {
        hunterMap.remove(hunter.getKey());
        batchComplete(hunter);
    }

    private void performBatchCompleteAndBatchCancel() {
        List<TaskHunter> copy = new ArrayList<>(batchComplete);
        batchComplete.clear();
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_COMPLETE, copy));

        List<Task> batchCancelCopy = new ArrayList<>(batchCancel);
        batchCancel.clear();
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_CANCEL, batchCancelCopy));
    }


    private void performRetry(TaskHunter hunter) {
        if (hunter.isCancelled()) return;

        if (service.isShutdown()) {
            performError(hunter);
            return;
        }

        NetworkInfo networkInfo = null;
        if (scansNetworkChanges) {
            ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        boolean hasConnectivity = networkInfo != null && networkInfo.isConnected();
        boolean shouldRetryHunter = hunter.shouldRetry(airplaneMode, networkInfo);
//        boolean supportsReplay = hunter.supportsReplay();


        if (!shouldRetryHunter) {
            performError(hunter);
            return;
        }


        // If we don't scan for network changes (missing permission) or if we have connectivity, retry.
        if (!scansNetworkChanges || hasConnectivity) {
            Future future = service.submit(hunter);
            hunter.setFuture(future);
            return;
        }

        performError(hunter);

//        if (supportsReplay) {
//            markForReplay(hunter);
//        }
    }

    private void performError(TaskHunter hunter) {
        hunterMap.remove(hunter.getKey());
        dispatchError(ErrorInformer.create(hunter.getTask().getCallback(),
                new SaultException(hunter.getKey(),
                        hunter.getTask().getUri().toString(),
                        hunter.getException())));
        batchCancel(hunter.getTask());
    }


    private void performAirplaneModeChange(boolean airplaneMode) {
        this.airplaneMode = airplaneMode;
    }

    private void performNetworkStateChange(NetworkInfo info) {
        if (service instanceof SaultExecutorService && isAutoAdjustThreadEnabled()) {
            ((SaultExecutorService) service).adjustThreadCount(info);
        }
        // Intentionally check only if isConnected() here before we flush out failed actions.
        if (info != null && info.isConnected()) {
//            flushFailedActions();
        }
    }


    private void batchComplete(TaskHunter hunter) {
        if (hunter.isCancelled()) {
            return;
        }
        batchComplete.add(hunter);
        if (!handler.hasMessages(HUNTER_DELAY_NEXT_BATCH)) {
            handler.sendEmptyMessageDelayed(HUNTER_DELAY_NEXT_BATCH, BATCH_DELAY);
        }
    }

    private void batchCancel(Task task) {
        batchCancel.add(task);
        if (!handler.hasMessages(HUNTER_DELAY_NEXT_BATCH)) {
            handler.sendEmptyMessageDelayed(HUNTER_DELAY_NEXT_BATCH, BATCH_DELAY);
        }
    }

    private static class DispatcherHandler extends Handler {
        private final Dispatcher dispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case TASK_SUBMIT: {
                    Task task = (Task) msg.obj;
                    dispatcher.performSubmit(task);
                    break;
                }
                case TASK_PAUSE: {
                    Object tag = msg.obj;
                    dispatcher.performPause(tag);
                    break;
                }
                case TASK_RESUME: {
                    Object tag = msg.obj;
                    dispatcher.performResume(tag);
                    break;
                }
                case TASK_CANCEL: {
                    Task task = (Task) msg.obj;
                    dispatcher.performCancel(task);
                    break;
                }
                case HUNTER_COMPLETE: {
                    TaskHunter hunter = (TaskHunter) msg.obj;
                    dispatcher.performComplete(hunter);
                    break;
                }
                case HUNTER_FAILED: {
                    TaskHunter hunter = (TaskHunter) msg.obj;
                    dispatcher.performError(hunter);
                    break;
                }
                case HUNTER_RETRY: {
                    TaskHunter hunter = (TaskHunter) msg.obj;
                    dispatcher.performRetry(hunter);
                    break;
                }
                case HUNTER_DELAY_NEXT_BATCH: {
                    dispatcher.performBatchCompleteAndBatchCancel();
                    break;
                }

                case NETWORK_STATE_CHANGE: {
                    NetworkInfo info = (NetworkInfo) msg.obj;
                    dispatcher.performNetworkStateChange(info);
                    break;
                }
                case AIRPLANE_MODE_CHANGE: {
                    dispatcher.performAirplaneModeChange(msg.arg1 == AIRPLANE_MODE_ON);
                    break;
                }
                default:
                    Sault.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            throw new AssertionError("Unknown handler message received: " + msg.what);
                        }
                    });
            }
        }
    }

    private static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super(THREAD_PREFIX + DISPATCHER_THREAD_NAME, THREAD_PRIORITY_BACKGROUND);
        }
    }


    static class NetworkBroadcastReceiver extends BroadcastReceiver {
        static final String EXTRA_AIRPLANE_STATE = "state";

        private final Dispatcher dispatcher;

        NetworkBroadcastReceiver(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        void register() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
            if (dispatcher.scansNetworkChanges) {
                filter.addAction(CONNECTIVITY_ACTION);
            }
            dispatcher.context.registerReceiver(this, filter);
        }

        void unregister() {
            dispatcher.context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // On some versions of Android this may be called with a null Intent,
            // also without extras (getExtras() == null), in such case we use defaults.
            if (intent == null) {
                return;
            }
            final String action = intent.getAction();
            if (ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
                if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
                    return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
                }
                dispatcher.dispatchAirplaneModeChange(intent.getBooleanExtra(EXTRA_AIRPLANE_STATE, false));
            } else if (CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
                dispatcher.dispatchNetworkStateChange(connectivityManager.getActiveNetworkInfo());
            }
        }
    }
}
