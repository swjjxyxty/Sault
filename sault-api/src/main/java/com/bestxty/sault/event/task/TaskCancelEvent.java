package com.bestxty.sault.event.task;

import com.bestxty.sault.Task;

/**
 * @author xty
 * Created by xty on 2017/10/5.
 */
public class TaskCancelEvent extends TaskEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param task The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public TaskCancelEvent(Task task) {
        super(task);
    }
}
