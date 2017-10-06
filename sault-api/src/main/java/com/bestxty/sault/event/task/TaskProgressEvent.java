package com.bestxty.sault.event.task;

import com.bestxty.sault.Task;

/**
 * @author xty
 * Created by xty on 2017/10/6.
 */
public class TaskProgressEvent extends TaskEvent{

    private long progress;

    public TaskProgressEvent(Task task, long progress) {
        super(task);
        this.progress = progress;
    }

    public long getProgress() {
        return progress;
    }
}
