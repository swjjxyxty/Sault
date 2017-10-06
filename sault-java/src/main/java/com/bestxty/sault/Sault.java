package com.bestxty.sault;

import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.HunterEventDispatcher;
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
public class Sault extends EventSupportSault {

    private Map<String, Task> taskMap = new ConcurrentHashMap<>();

    public Sault(HunterEventDispatcher hunterEventDispatcher) {
        super(hunterEventDispatcher);

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
        dispatcherEvent(new TaskSubmitEvent(task));
    }

    public void pause(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        dispatcherEvent(new TaskPauseEvent(task));
    }

    public void cancel(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        dispatcherEvent(new TaskCancelEvent(task));
    }

    public void resume(String taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) return;
        dispatcherEvent(new TaskResumeEvent(task));
    }


    private void performTaskSplit(TaskSplitEvent event) {
        TaskWrapper taskWrapper = ((TaskWrapper) event.getSource());
        taskMap.put(taskWrapper.getTaskId(), taskWrapper);
        List<SplitTask> splitTasks = taskWrapper.getSplitTasks();
        for (SplitTask splitTask : splitTasks) {
            dispatcherEvent(new TaskSubmitEvent(splitTask));
        }
    }

}
