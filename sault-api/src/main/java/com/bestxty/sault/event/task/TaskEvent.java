package com.bestxty.sault.event.task;

import com.bestxty.sault.Task;
import com.bestxty.sault.event.Event;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class TaskEvent extends Event {
    /**
     * Constructs a prototypical Event.
     *
     * @param task The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public TaskEvent(Task task) {
        super(task);
    }
}
