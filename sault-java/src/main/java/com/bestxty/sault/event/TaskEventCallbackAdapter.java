package com.bestxty.sault.event;

import com.bestxty.sault.event.task.TaskCancelEvent;
import com.bestxty.sault.event.task.TaskCompleteEvent;
import com.bestxty.sault.event.task.TaskPauseEvent;
import com.bestxty.sault.event.task.TaskResumeEvent;
import com.bestxty.sault.event.task.TaskSubmitEvent;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class TaskEventCallbackAdapter extends SimpleEventDispatcher {

    private TaskEventDispatcher taskEventDispatcher;

    public TaskEventCallbackAdapter(EventCallbackExecutor eventCallbackExecutor) {
        super(eventCallbackExecutor);
    }


    public void setTaskEventDispatcher(TaskEventDispatcher taskEventDispatcher) {
        if (taskEventDispatcher == null) {
            throw new IllegalArgumentException("");
        }
        this.taskEventDispatcher = taskEventDispatcher;
        registerCallbacks();
    }


    private void registerCallbacks() {
        taskEventDispatcher.addEventCallback(new EventCallback<TaskSubmitEvent>() {
            @Override
            public void onEvent(TaskSubmitEvent event) {
                performTaskSubmit(event);
            }
        });
        taskEventDispatcher.addEventCallback(new EventCallback<TaskPauseEvent>() {
            @Override
            public void onEvent(TaskPauseEvent event) {
                performTaskPause(event);
            }
        });
        taskEventDispatcher.addEventCallback(new EventCallback<TaskCancelEvent>() {
            @Override
            public void onEvent(TaskCancelEvent event) {
                performTaskCancel(event);
            }
        });
        taskEventDispatcher.addEventCallback(new EventCallback<TaskResumeEvent>() {
            @Override
            public void onEvent(TaskResumeEvent event) {
                performTaskResume(event);
            }
        });
        taskEventDispatcher.addEventCallback(new EventCallback<TaskCompleteEvent>() {
            @Override
            public void onEvent(TaskCompleteEvent event) {
                performTaskComplete(event);
            }
        });
    }

    abstract void performTaskSubmit(TaskSubmitEvent event);

    abstract void performTaskPause(TaskPauseEvent event);

    abstract void performTaskCancel(TaskCancelEvent event);

    abstract void performTaskResume(TaskResumeEvent event);

    abstract void performTaskComplete(TaskCompleteEvent event);
}
