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
public interface Task {
    String getTaskId();

    URI getUri();

    Map<String, String> getHeaderMap();

    File getTarget();

    Priority getPriority();

    List<EventCallback<?>> getEventCallbacks();

    TraceMeta getTraceMeta();

    AdvancedProperty getAdvancedProperty();

    class TraceMeta {
        private long startTime;
        private long finishTime;
        private long threadCount;

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }

        public long getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(long threadCount) {
            this.threadCount = threadCount;
        }

        @Override
        public String toString() {
            return "TraceMeta{" +
                    "startTime=" + startTime +
                    ", finishTime=" + finishTime +
                    ", threadCount=" + threadCount +
                    '}';
        }
    }


    class AdvancedProperty {
        private boolean multiThreadEnabled;
        private boolean breakPointEnabled;
        private boolean retryEnabled;

        public boolean isMultiThreadEnabled() {
            return multiThreadEnabled;
        }

        public void setMultiThreadEnabled(boolean multiThreadEnabled) {
            this.multiThreadEnabled = multiThreadEnabled;
        }

        public boolean isBreakPointEnabled() {
            return breakPointEnabled;
        }

        public void setBreakPointEnabled(boolean breakPointEnabled) {
            this.breakPointEnabled = breakPointEnabled;
        }

        public boolean isRetryEnabled() {
            return retryEnabled;
        }

        public void setRetryEnabled(boolean retryEnabled) {
            this.retryEnabled = retryEnabled;
        }

        @Override
        public String toString() {
            return "AdvancedProperty{" +
                    "multiThreadEnabled=" + multiThreadEnabled +
                    ", breakPointEnabled=" + breakPointEnabled +
                    ", retryEnabled=" + retryEnabled +
                    '}';
        }
    }
}
