package com.bestxty.sault;

import com.bestxty.sault.event.DefaultEventCallbackExecutor;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.EventCallbackExecutor;
import com.bestxty.sault.utils.Utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class HunterTask extends EventSupportTask {

    private String taskId;
    private final URI uri;
    private final Map<String, String> headerMap;
    private final File target;
    private final Priority priority;
    private final List<EventCallback<?>> callbacks;
    private final TraceMeta traceMeta;
    private final AdvancedProperty advancedProperty;


    public HunterTask(EventCallbackExecutor callbackExecutor, URI uri, Map<String, String> headerMap,
                      File target, Priority priority, List<EventCallback<?>> callbacks,
                      TraceMeta traceMeta, AdvancedProperty advancedProperty) {
        super(callbackExecutor);
        this.uri = uri;
        this.headerMap = headerMap;
        this.target = target;
        this.priority = priority;
        this.callbacks = callbacks;
        this.traceMeta = traceMeta;
        this.advancedProperty = advancedProperty;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    @Override
    public File getTarget() {
        return target;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public List<EventCallback<?>> getEventCallbacks() {
        return callbacks;
    }

    @Override
    public TraceMeta getTraceMeta() {
        return traceMeta;
    }

    @Override
    public AdvancedProperty getAdvancedProperty() {
        return advancedProperty;
    }

    private void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public static final class Builder {

        private URI uri;
        private Map<String, String> headerMap;
        private File target;
        private Priority priority;
        private List<EventCallback<?>> callbacks;
        private boolean multiThreadEnabled = true;
        private boolean breakPointEnabled = true;
        private boolean retryEnabled = true;

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder uri(String url) {
            try {
                this.uri = new URI(url);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            return this;
        }

        public Builder header(String key, String value) {
            if (this.headerMap == null) {
                headerMap = new HashMap<>();
            }
            headerMap.put(key, value);
            return this;
        }

        public Builder header(Map<String, String> headers) {
            this.headerMap = headers;
            return this;
        }


        public Builder target(File target) {
            this.target = target;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder callback(EventCallback<?> eventCallback) {
            if (this.callbacks == null) {
                this.callbacks = new ArrayList<>();
            }
            if (!this.callbacks.contains(eventCallback)) {
                this.callbacks.add(eventCallback);
            }
            return this;
        }

        public Builder multiThreadEnabled(boolean multiThreadEnabled) {
            this.multiThreadEnabled = multiThreadEnabled;
            return this;
        }


        public Builder breakPointEnabled(boolean breakPointEnabled) {
            this.breakPointEnabled = breakPointEnabled;
            return this;
        }


        public Builder retryEnabled(boolean retryEnabled) {
            this.retryEnabled = retryEnabled;
            return this;
        }


        public HunterTask build() {
            if (uri == null) {
                throw new IllegalStateException("");
            }
            priority = priority == null ? Priority.NORMAL : priority;
            headerMap = headerMap == null ? Collections.<String, String>emptyMap() : headerMap;
            callbacks = callbacks == null ? Collections.<EventCallback<?>>emptyList() : callbacks;


            AdvancedProperty advancedProperty = new AdvancedProperty();
            advancedProperty.setBreakPointEnabled(breakPointEnabled);
            advancedProperty.setMultiThreadEnabled(multiThreadEnabled);
            advancedProperty.setRetryEnabled(retryEnabled);

            HunterTask task = new HunterTask(new DefaultEventCallbackExecutor(""), uri,
                    Collections.unmodifiableMap(headerMap), target,
                    priority, Collections.unmodifiableList(callbacks),
                    new TraceMeta(), advancedProperty);
            task.setTaskId(Utils.buildTaskId(task));
            return task;
        }
    }
}
