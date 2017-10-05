package com.bestxty.sault.event;

import com.bestxty.sault.CancelableHunter;
import com.bestxty.sault.SaultExecutorService;
import com.bestxty.sault.SplitTask;
import com.bestxty.sault.Task;
import com.bestxty.sault.TaskWrapper;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.event.hunter.HunterCancelEvent;
import com.bestxty.sault.event.hunter.HunterPauseEvent;
import com.bestxty.sault.event.task.TaskCancelEvent;
import com.bestxty.sault.event.task.TaskCompleteEvent;
import com.bestxty.sault.event.task.TaskPauseEvent;
import com.bestxty.sault.event.task.TaskResumeEvent;
import com.bestxty.sault.event.task.TaskSubmitEvent;
import com.bestxty.sault.hunter.SimpleHunter;
import com.bestxty.sault.hunter.TaskSplitHunter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class HunterEventDispatcher extends TaskEventCallbackAdapter {

    private SaultExecutorService executorService;
    private Downloader downloader;

    private Set<String> pausedTaskIds = new HashSet<>();
    private Map<String, CancelableHunter> hunterMap = new ConcurrentHashMap<>();

    public HunterEventDispatcher(EventCallbackExecutor eventCallbackExecutor,
                                 SaultExecutorService executorService,
                                 Downloader downloader) {
        super(eventCallbackExecutor);
        this.executorService = executorService;
        this.downloader = downloader;
    }

    private CancelableHunter buildHunter(Task task) {
        return task instanceof SplitTask ? new SimpleHunter(downloader, this, task)
                : new TaskSplitHunter(downloader, this, task);
    }

    @Override
    void performTaskSubmit(TaskSubmitEvent event) {
        Task task = (Task) event.getSource();
        CancelableHunter hunter = buildHunter(task);
        hunterMap.put(task.getTaskId(), hunter);
        Future<?> future = executorService.submit(hunter);
        hunter.setFuture(future);
    }

    @Override
    void performTaskPause(TaskPauseEvent event) {
        Task task = (Task) event.getSource();
        if (!pausedTaskIds.add(task.getTaskId())) {
            return;
        }

        if (task instanceof TaskWrapper) {
            TaskWrapper wrapper = ((TaskWrapper) task);
            for (SplitTask splitTask : wrapper.getSplitTasks()) {
                System.err.println("splitTask.getTaskId() = " + splitTask.getTaskId());
                CancelableHunter hunter = hunterMap.get(splitTask.getTaskId());
                if (hunter.cancel()) {
                    hunterMap.remove(splitTask.getTaskId());
                    dispatcherEvent(new HunterPauseEvent(hunter));
                }
            }
        }

    }

    @Override
    void performTaskCancel(TaskCancelEvent event) {
        Task task = (Task) event.getSource();
        if (task instanceof TaskWrapper) {
            TaskWrapper wrapper = ((TaskWrapper) task);
            for (SplitTask splitTask : wrapper.getSplitTasks()) {
                CancelableHunter hunter = hunterMap.get(splitTask.getTaskId());
                if (hunter.cancel()) {
                    hunterMap.remove(splitTask.getTaskId());
                    dispatcherEvent(new HunterCancelEvent(hunter));
                }
            }
        }
    }

    @Override
    void performTaskResume(TaskResumeEvent event) {
        Task task = (Task) event.getSource();
        if (pausedTaskIds.contains(task.getTaskId())) {
            if (task instanceof TaskWrapper) {
                TaskWrapper wrapper = ((TaskWrapper) task);
                for (SplitTask splitTask : wrapper.getSplitTasks()) {
                    performTaskSubmit(new TaskSubmitEvent(splitTask));
                }
            }
        }
    }

    @Override
    void performTaskComplete(TaskCompleteEvent event) {
    }

}
