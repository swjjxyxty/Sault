package com.bestxty.sault.task;

import android.net.Uri;

import com.bestxty.sault.Callback;
import com.bestxty.sault.Sault;
import com.bestxty.sault.Sault.Priority;

import java.io.File;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface SaultTask {

    Sault getSault();

    int getId();

    String getKey();

    Object getTag();

    Callback getCallback();

    Priority getPriority();

    Uri getUri();

    File getTarget();

    boolean isBreakPointEnabled();

    Trace getTrace();

    void setStartTime(long startTime);

    void setEndTime(long endTime);

    Progress getProgress();

    void setTotalSize(long totalSize);

    void notifyFinishedSize(long stepSize);

    class Trace {
        private final long createTime;
        private long startTime;
        private long endTime;

        public Trace() {
            this.createTime = System.currentTimeMillis();
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    class Progress {
        private final long totalSize;
        private final long finishedSize;

        public Progress(long totalSize, long finishedSize) {
            this.totalSize = totalSize;
            this.finishedSize = finishedSize;
        }

        public long getFinishedSize() {
            return finishedSize;
        }

        public long getTotalSize() {
            return totalSize;
        }
    }

}
