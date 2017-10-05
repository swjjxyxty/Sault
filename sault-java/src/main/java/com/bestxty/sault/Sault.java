package com.bestxty.sault;

import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.HunterEventDispatcher;
import com.bestxty.sault.event.TaskEventDispatcher;
import com.bestxty.sault.event.task.TaskCancelEvent;
import com.bestxty.sault.event.task.TaskPauseEvent;
import com.bestxty.sault.event.task.TaskResumeEvent;
import com.bestxty.sault.event.task.TaskSplitEvent;
import com.bestxty.sault.event.task.TaskSubmitEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bestxty.sault.utils.Utils.log;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class Sault {

    private TaskEventDispatcher taskEventDispatcher;
    private HunterEventDispatcher hunterEventDispatcher;

    private Map<String, Task> taskMap = new ConcurrentHashMap<>();

    public Sault(TaskEventDispatcher taskEventDispatcher,
                 HunterEventDispatcher hunterEventDispatcher) {
        this.taskEventDispatcher = taskEventDispatcher;
        this.hunterEventDispatcher = hunterEventDispatcher;

        this.taskEventDispatcher.setHunterEventDispatcher(this.hunterEventDispatcher);
        this.hunterEventDispatcher.setTaskEventDispatcher(this.taskEventDispatcher);
        hunterEventDispatcher.addEventCallback(new EventCallback<TaskSplitEvent>() {
            @Override
            public void onEvent(TaskSplitEvent event) {
                performTaskSplit(event);
            }
        });
    }

    public void submit(Task task) {
        log("submit task:" + task.getTaskId());
        taskMap.put(task.getTaskId(), task);
        taskEventDispatcher.dispatcherEvent(new TaskSubmitEvent(task));
    }

    public void pause(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        taskEventDispatcher.dispatcherEvent(new TaskPauseEvent(task));
    }

    public void cancel(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        taskEventDispatcher.dispatcherEvent(new TaskCancelEvent(task));
    }

    public void resume(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        taskEventDispatcher.dispatcherEvent(new TaskResumeEvent(task));
    }


    private void performTaskSplit(TaskSplitEvent event) {
        TaskWrapper taskWrapper = ((TaskWrapper) event.getSource());
        taskMap.put(taskWrapper.getTaskId(), taskWrapper);
        List<SplitTask> splitTasks = taskWrapper.getSplitTasks();
        for (SplitTask splitTask : splitTasks) {
            taskEventDispatcher.dispatcherEvent(new TaskSubmitEvent(splitTask));
        }
    }

}
