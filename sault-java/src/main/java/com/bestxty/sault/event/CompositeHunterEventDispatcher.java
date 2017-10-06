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
public abstract class CompositeHunterEventDispatcher extends SimpleEventDispatcher {


    public CompositeHunterEventDispatcher(EventCallbackExecutor eventCallbackExecutor) {
        super(eventCallbackExecutor);
        registerCallbacks();
    }


    private void registerCallbacks() {
        addEventCallback(new EventCallback<TaskSubmitEvent>() {
            @Override
            public void onEvent(TaskSubmitEvent event) {
                performTaskSubmit(event);
            }
        });
        addEventCallback(new EventCallback<TaskPauseEvent>() {
            @Override
            public void onEvent(TaskPauseEvent event) {
                performTaskPause(event);
            }
        });
        addEventCallback(new EventCallback<TaskCancelEvent>() {
            @Override
            public void onEvent(TaskCancelEvent event) {
                performTaskCancel(event);
            }
        });
        addEventCallback(new EventCallback<TaskResumeEvent>() {
            @Override
            public void onEvent(TaskResumeEvent event) {
                performTaskResume(event);
            }
        });
        addEventCallback(new EventCallback<TaskCompleteEvent>() {
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
