package com.bestxty.sault;

import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.utils.Utils;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class SplitHunterTask extends EventSupportTask implements SplitTask {
    private final Task task;
    private final long startPosition;
    private final long endPosition;
    private final String taskId;

    public SplitHunterTask(Task task, long startPosition, long endPosition) {
        super(task.getEventCallbackExecutor());
        this.task = task;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.taskId = Utils.buildTaskId(this);
    }

    @Override
    public long getStartPosition() {
        return startPosition;
    }

    @Override
    public long getEndPosition() {
        return endPosition;
    }

    @Override
    public String getTaskId() {
        return taskId;
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
