package com.bestxty.sault.internal.handler;

import android.net.NetworkInfo;

import com.bestxty.sault.NetworkStatusProvider;
import com.bestxty.sault.internal.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.hunter.TaskHunterFactory;
import com.bestxty.sault.internal.task.ExceptionSaultTask;
import com.bestxty.sault.internal.task.SaultTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */
@Singleton
public class DefaultEventHandler implements TaskRequestEventHandler, HunterEventHandler {

    private final Map<Integer, TaskHunter> hunterMap = new ConcurrentHashMap<>();
    private final Map<String, List<SaultTask>> pausedTaskMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final NetworkStatusProvider networkStatusProvider;
    private final SaultTaskEventDispatcher taskEventDispatcher;

    @Inject
    public DefaultEventHandler(ExecutorService executorService,
                               NetworkStatusProvider networkStatusProvider,
                               SaultTaskEventDispatcher taskEventDispatcher) {
        this.executorService = executorService;
        this.networkStatusProvider = networkStatusProvider;
        this.taskEventDispatcher = taskEventDispatcher;
    }


    @Override
    public void handleSaultTaskSubmitRequest(SaultTask task) {
        TaskHunter hunter = TaskHunterFactory.newTaskHunter(task);
        Future<?> future = executorService.submit(hunter);
        hunter.setFuture(future);
        hunterMap.put(hunter.getSequence(), hunter);
    }

    @Override
    public void handleSaultTaskPauseRequest(SaultTask task) {
        List<SaultTask> tasks = pausedTaskMap.get(task.getKey());
        if (tasks != null) {
            return;
        }
        List<Integer> hunterSequences = getHunterSequences(task);
        List<SaultTask> canceledTasks = cancelHunters(hunterSequences);
        pausedTaskMap.put(task.getKey(), canceledTasks);
        taskEventDispatcher.dispatchSaultTaskPause(task);
    }

    @Override
    public void handleSaultTaskResumeRequest(SaultTask task) {
        List<SaultTask> tasks = pausedTaskMap.get(task.getKey());
        if (tasks == null) {
            return;
        }
        for (SaultTask saultTask : tasks) {
            handleSaultTaskSubmitRequest(saultTask);
        }
        pausedTaskMap.remove(task.getKey());
        taskEventDispatcher.dispatchSaultTaskResume(task);
    }

    @Override
    public void handleSaultTaskCancelRequest(SaultTask task) {
        List<SaultTask> tasks = pausedTaskMap.get(task.getKey());
        if (tasks != null) {
            pausedTaskMap.remove(task.getKey());
            taskEventDispatcher.dispatchSaultTaskCancel(task);
            return;
        }
        List<Integer> hunterSequences = getHunterSequences(task);
        cancelHunters(hunterSequences).clear();
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
        if (hunter.isCancelled())
            removeSelfAndDispatchException(hunter);
    }

    @Override
    public void handleHunterFinish(TaskHunter hunter) {
        if (TaskHunterFactory.isPartingSaultTaskHunter(hunter)) {
            hunterMap.remove(hunter.getSequence());
            return;
        }

        SaultTask task = hunter.getTask();
        SaultTask.Progress progress = task.getProgress();
        if (progress.getTotalSize() == progress.getFinishedSize()) {
            task.setEndTime(System.nanoTime());
            taskEventDispatcher.dispatchSaultTaskComplete(task);
        }
        hunterMap.remove(hunter.getSequence());
    }

    @Override
    public void handleHunterFailed(TaskHunter hunter) {
        if (hunter.isCancelled()) return;
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


    private List<SaultTask> cancelHunters(List<Integer> hunterSequences) {
        List<SaultTask> canceledTask = new ArrayList<>(hunterSequences.size());
        for (Integer hunterSequence : hunterSequences) {
            TaskHunter taskHunter = hunterMap.get(hunterSequence);
            if (taskHunter.cancel()) {
                hunterMap.remove(hunterSequence);
                canceledTask.add(taskHunter.getTask());
            }
        }
        return canceledTask;
    }
}
