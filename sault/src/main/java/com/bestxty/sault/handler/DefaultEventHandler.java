package com.bestxty.sault.handler;

import android.net.NetworkInfo;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.NetworkStatusProvider;
import com.bestxty.sault.dispatcher.AbstractCompositeEventDispatcher;
import com.bestxty.sault.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.hunter.DefaultSaultTaskHunter;
import com.bestxty.sault.hunter.PartingSaultTaskHunter;
import com.bestxty.sault.hunter.TaskHunter;
import com.bestxty.sault.task.ExceptionSaultTask;
import com.bestxty.sault.task.PartedSaultTask;
import com.bestxty.sault.task.SaultTask;
import com.bestxty.sault.task.SaultTask.Progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */

public class DefaultEventHandler implements TaskRequestEventHandler, HunterEventHandler {

    private final Map<Integer, TaskHunter> hunterMap = new ConcurrentHashMap<>();
    private final Map<String, SaultTask> pausedTaskMap = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private Downloader downloader;
    private TaskRequestEventDispatcher taskRequestEventDispatcher;
    private SaultTaskEventDispatcher taskEventDispatcher;
    private HunterEventDispatcher hunterEventDispatcher;
    private NetworkStatusProvider networkStatusProvider;

    public DefaultEventHandler(ExecutorService executorService,
                               Downloader downloader,
                               AbstractCompositeEventDispatcher eventDispatcher,
                               NetworkStatusProvider networkStatusProvider) {
        this.executorService = executorService;
        this.downloader = downloader;
        this.taskEventDispatcher = eventDispatcher;
        this.taskRequestEventDispatcher = eventDispatcher;
        this.hunterEventDispatcher = eventDispatcher;
        this.networkStatusProvider = networkStatusProvider;
    }

    private TaskHunter newTaskHunter(SaultTask task) {
        if (task instanceof PartedSaultTask) {
            return new DefaultSaultTaskHunter(((PartedSaultTask) task),
                    downloader, hunterEventDispatcher);
        }
        return new PartingSaultTaskHunter(task,
                downloader, hunterEventDispatcher, taskEventDispatcher,
                taskRequestEventDispatcher);
    }

    @Override
    public void handleSaultTaskSubmitRequest(SaultTask task) {
        TaskHunter hunter = newTaskHunter(task);
        Future<?> future = executorService.submit(hunter);
        hunter.setFuture(future);
        hunterMap.put(hunter.getSequence(), hunter);
    }

    @Override
    public void handleSaultTaskPauseRequest(SaultTask task) {
        SaultTask saultTask = pausedTaskMap.get(task.getKey());
        if (saultTask != null) {
            return;
        }
        List<Integer> hunterSequences = getHunterSequences(task);
        cancelHunters(hunterSequences);
        pausedTaskMap.put(task.getKey(), task);
        taskEventDispatcher.dispatchSaultTaskPause(task);
    }

    @Override
    public void handleSaultTaskResumeRequest(SaultTask task) {
        SaultTask saultTask = pausedTaskMap.get(task.getKey());
        if (saultTask == null) {
            return;
        }
        handleSaultTaskSubmitRequest(task);
        pausedTaskMap.remove(task.getKey());
        taskEventDispatcher.dispatchSaultTaskResume(task);
    }

    @Override
    public void handleSaultTaskCancelRequest(SaultTask task) {
        SaultTask saultTask = pausedTaskMap.get(task.getKey());
        if (saultTask != null) {
            pausedTaskMap.remove(task.getKey());
            return;
        }
        List<Integer> hunterSequences = getHunterSequences(task);
        cancelHunters(hunterSequences);
        taskEventDispatcher.dispatchSaultTaskCancel(task);
    }


    @Override
    public void handleHunterStart(TaskHunter hunter) {
        taskEventDispatcher.dispatchSaultTaskStart(hunter.getTask());
    }

    @Override
    public void handleHunterRetry(TaskHunter hunter) {
        if (hunter.isCancelled()) {
            return;
        }
        if (executorService.isShutdown()) {
            removeSelfAndDispatchException(hunter);
            return;
        }


        NetworkInfo networkInfo = null;
        if (networkStatusProvider.accessNetwork()) {
            networkInfo = networkStatusProvider.getNetworkInfo();
        }
        boolean hasConnectivity = networkInfo != null && networkInfo.isConnected();
        boolean shouldRetryHunter = hunter.shouldRetry(networkStatusProvider.isAirplaneMode(),
                networkInfo);

        if (!shouldRetryHunter) {
            removeSelfAndDispatchException(hunter);
            return;
        }

        if (!networkStatusProvider.accessNetwork() || hasConnectivity) {
            Future future = executorService.submit(hunter);
            hunter.setFuture(future);
            return;
        }

        removeSelfAndDispatchException(hunter);

    }

    @Override
    public void handleHunterException(TaskHunter hunter) {
        removeSelfAndDispatchException(hunter);
    }

    @Override
    public void handleHunterFinish(TaskHunter hunter) {
        if (hunter instanceof PartingSaultTaskHunter) {
            hunterMap.remove(hunter.getSequence());
            return;
        }

        SaultTask task = hunter.getTask();
        Progress progress = task.getProgress();
        if (progress.getTotalSize() == progress.getFinishedSize()) {
            task.setEndTime(System.nanoTime());
            taskEventDispatcher.dispatchSaultTaskComplete(task);
        }
        hunterMap.remove(hunter.getSequence());
    }

    @Override
    public void handleHunterFailed(TaskHunter hunter) {
        removeSelfAndDispatchException(hunter);
    }

    private void removeSelfAndDispatchException(TaskHunter hunter) {
        SaultTask task = hunter.getTask();
        List<Integer> hunterSequences = getHunterSequences(task);
        cancelHunters(hunterSequences);
        taskEventDispatcher
                .dispatchSaultTaskException(new ExceptionSaultTask(task, hunter.getException()));

    }

    private List<Integer> getHunterSequences(SaultTask task) {
        String taskKey = task.getKey();
        List<Integer> hunterSequences = new ArrayList<>();
        for (TaskHunter taskHunter : hunterMap.values()) {
            if (taskHunter.getTask().getKey().equals(taskKey)) {
                hunterSequences.add(taskHunter.getSequence());
            }
        }
        return hunterSequences;
    }

    private void cancelHunters(List<Integer> hunterSequences) {
        for (Integer hunterSequence : hunterSequences) {
            TaskHunter taskHunter = hunterMap.get(hunterSequence);
            if (taskHunter.cancel()) {
                hunterMap.remove(hunterSequence);
            }
        }
    }
}
