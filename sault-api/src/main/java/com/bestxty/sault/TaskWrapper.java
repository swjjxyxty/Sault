package com.bestxty.sault;

import com.bestxty.sault.event.EventCallback;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class TaskWrapper implements Task {
    private Task task;
    private List<SplitTask> splitTasks;

    public TaskWrapper(Task task, List<SplitTask> splitTasks) {
        this.task = task;
        this.splitTasks = splitTasks;
    }

    public List<SplitTask> getSplitTasks() {
        return splitTasks;
    }

    @Override
    public String getTaskId() {
        return task.getTaskId();
    }

    @Override
    public URI getUri() {
        return task.getUri();
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return task.getHeaderMap();
    }

    @Override
    public File getTarget() {
        return task.getTarget();
    }

    @Override
    public Priority getPriority() {
        return task.getPriority();
    }

    @Override
    public List<EventCallback<?>> getEventCallbacks() {
        return task.getEventCallbacks();
    }

    @Override
    public TraceMeta getTraceMeta() {
        return task.getTraceMeta();
    }

    @Override
    public AdvancedProperty getAdvancedProperty() {
        return task.getAdvancedProperty();
    }
}
