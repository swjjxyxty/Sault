package com.bestxty.dl;

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
import static com.bestxty.dl.Callback.EVENT_CANCEL;
import static com.bestxty.dl.Callback.EVENT_PAUSE;
import static com.bestxty.dl.Callback.EVENT_START;
import static com.bestxty.dl.Utils.DISPATCHER_THREAD_NAME;
import static com.bestxty.dl.Utils.ErrorInformer;
import static com.bestxty.dl.Utils.EventInformer;
import static com.bestxty.dl.Utils.ProgressInformer;
import static com.bestxty.dl.Utils.THREAD_PREFIX;
import static com.bestxty.dl.Utils.getService;
import static com.bestxty.dl.Utils.hasPermission;

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
    static final int TASK_EVENT = 5;
    static final int HUNTER_COMPLETE = 6;
    static final int HUNTER_FAILED = 7;
    static final int HUNTER_RETRY = 8;
    static final int TASK_BATCH_RESUME = 9;
    static final int HUNTER_DELAY_NEXT_BATCH = 10;
    static final int HUNTER_BATCH_COMPLETE = 11;
    static final int HUNTER_PROGRESS = 12;
    static final int HUNTER_ERROR = 13;
    static final int NETWORK_STATE_CHANGE = 14;
    static final int AIRPLANE_MODE_CHANGE = 15;

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
    private final List<TaskHunter> batch;

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
        this.batch = new ArrayList<>(4);
        this.airplaneMode = Utils.isAirplaneModeOn(this.context);
        this.scansNetworkChanges = hasPermission(context, ACCESS_NETWORK_STATE);
        this.receiver = new NetworkBroadcastReceiver(this);
        receiver.register();
    }

    boolean isAutoAdjustThreadEnabled() {
        return autoAdjustThreadEnabled;
    }

    void shutdown() {
        // Shutdown the thread pool only if it is the one created by Picasso.
        if (service instanceof SaultExecutorService) {
            service.shutdown();
        }
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
        stats.batchSize = batch.size();
        stats.failedTaskSize = failedTaskMap.size();
        stats.pausedTagSize = pausedTags.size();
        stats.pausedTaskSize = pausedTaskMap.size();
        return stats;
    }

    Future submit(Runnable runnable) {
        return service.submit(runnable);
    }

    private void dispatchError(ErrorInformer errorInformer) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_ERROR, errorInformer));
    }

    private void dispatchEvent(EventInformer eventInformer) {
        System.out.println("dispatch event." + eventInformer.event);
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TASK_EVENT, eventInformer));
    }

    void dispatchProgress(ProgressInformer progressInformer) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_PROGRESS,
                ProgressInformer.from(progressInformer)));
    }

    void dispatchSubmit(Task task) {
        System.out.println("dispatch submit.task=" + task.getKey());
        handler.sendMessage(handler.obtainMessage(TASK_SUBMIT, task));
    }

    void dispatchPauseTag(Object tag) {
        System.out.println(String.format(Locale.CHINA, "dispatch pause. paused size=%d,paused tag=%d,failed size=%d,hunter size=%d.",
                pausedTaskMap.size(),
                pausedTags.size(),
                failedTaskMap.size(),
                hunterMap.size()));
        handler.sendMessage(handler.obtainMessage(TASK_PAUSE, tag));
    }

    void dispatchCancel(Task task) {
        System.out.println("dispatch cancel.");
        handler.sendMessage(handler.obtainMessage(TASK_CANCEL, task));
    }

    void dispatchResumeTag(Object tag) {
        handler.sendMessage(handler.obtainMessage(TASK_RESUME, tag));
    }

    void dispatchComplete(TaskHunter hunter) {
        handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
    }

    void dispatchFailed(TaskHunter hunter) {
        System.out.println("dispatch task failed. ex=" + hunter.getException().getMessage());
        hunter.getException().printStackTrace();
        handler.sendMessage(handler.obtainMessage(HUNTER_FAILED, hunter));
    }

    void dispatchRetry(TaskHunter hunter) {
        System.out.println("dispatch task retry.ex=" + hunter.getException().getMessage());
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
        System.out.println("perform submit task,task=" + task.getKey());
        TaskHunter hunter = buildTaskHunter(task);
        Future future = service.submit(hunter);
        hunter.setFuture(future);
        hunterMap.put(hunter.getKey(), hunter);
        System.out.println("put hunter to hunter map. size=" + hunterMap.size());
        dispatchEvent(EventInformer.fromTask(task, EVENT_START));
    }

    private TaskHunter buildTaskHunter(Task task) {
//        if (task.isMultiThreadEnabled()) {
//            return new MultiThreadTaskHunter(task, downloader, this);
//        }
        return new SaultTaskHunter(task.getSault(), this, task, downloader);
    }

    private void performPause(Object tag) {
        if (!pausedTags.add(tag)) {
            System.out.println("tag is already in paused tag set.");
            return;
        }

        System.out.println("ready pause task for tag:" + tag);
        for (Iterator<TaskHunter> iterator = hunterMap.values().iterator(); iterator.hasNext(); ) {
            TaskHunter hunter = iterator.next();
            Task single = hunter.getTask();
            if (single == null) {
                continue;
            }

            if (single.getTag().equals(tag)) {
                System.out.println("find task");
                System.out.println("detach task for hunter");
                if (hunter.cancel()) {
                    System.out.println("cancel hunter");
                    iterator.remove();
                    System.out.println("put task to paused task map");
                    pausedTaskMap.put(single.getKey(), single);
                    dispatchEvent(EventInformer.fromTask(single, EVENT_PAUSE));
                }
            }
        }
        System.out.println("perform pause finish");
    }

    private void performResume(Object tag) {
        if (!pausedTags.remove(tag)) {
            System.out.println("paused tag set not contain tag.");
            return;
        }
        System.out.println("ready resume task for tag:" + tag);
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
            System.out.println("find need resumed task size=" + batch.size());
            mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TASK_BATCH_RESUME, batch));
        } else {
            System.out.println("not found need resumed task");
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void performCancel(Task task) {
        System.out.println("perform cancel.");
        String key = task.getKey();
        TaskHunter hunter = hunterMap.get(key);
        if (hunter != null) {
            System.out.println("find hunter");
            if (hunter.cancel()) {
                System.out.println("cancel hunter");
                dispatchEvent(EventInformer.fromTask(task, EVENT_CANCEL));
                hunterMap.remove(key);
            }
        }

        if (pausedTags.contains(task.getTag())) {
            System.out.println("paused tags contain task");
            pausedTaskMap.remove(key);
            pausedTags.remove(task.getTag());
            dispatchEvent(EventInformer.fromTask(task, EVENT_CANCEL));
        }

        Task remove = failedTaskMap.remove(key);
        if (remove != null) {
            System.out.println("task removed from failed task map");
            dispatchEvent(EventInformer.fromTask(task, EVENT_CANCEL));
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void performComplete(TaskHunter hunter) {
        hunterMap.remove(hunter.getKey());
        batch(hunter);
    }

    private void performBatchComplete() {
        List<TaskHunter> copy = new ArrayList<>(batch);
        batch.clear();
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_COMPLETE, copy));
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
        dispatchError(ErrorInformer.fromTask(hunter.getTask().getCallback(),
                new DownloadException(hunter.getKey(),
                        hunter.getTask().getUri().toString(),
                        hunter.getException())));
        batch(hunter);
    }


    private void performAirplaneModeChange(boolean airplaneMode) {
        this.airplaneMode = airplaneMode;
    }

    private void performNetworkStateChange(NetworkInfo info) {
        if (service instanceof SaultExecutorService) {
            ((SaultExecutorService) service).adjustThreadCount(info);
        }
        // Intentionally check only if isConnected() here before we flush out failed actions.
        if (info != null && info.isConnected()) {
//            flushFailedActions();
        }
    }


    private void batch(TaskHunter hunter) {
        if (hunter.isCancelled()) {
            return;
        }
        batch.add(hunter);
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
                    dispatcher.performBatchComplete();
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
