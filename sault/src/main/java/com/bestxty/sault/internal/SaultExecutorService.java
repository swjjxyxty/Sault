package com.bestxty.sault.internal;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.bestxty.sault.internal.hunter.TaskHunter;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.bestxty.sault.Sault.Priority;
import static com.bestxty.sault.internal.Utils.DownloadThreadFactory;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public class SaultExecutorService extends ThreadPoolExecutor {

    private static final int DEFAULT_THREAD_COUNT = 3;


    public SaultExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MICROSECONDS,
                new PriorityBlockingQueue<Runnable>(), new DownloadThreadFactory());
    }


    public void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(DEFAULT_THREAD_COUNT);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(DEFAULT_THREAD_COUNT);
                }
                break;
            default:
                setThreadCount(DEFAULT_THREAD_COUNT);
        }
    }

    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public Future<?> submit(Runnable task) {
        TaskHunter taskHunter;
        try {
            taskHunter = (TaskHunter) task;
        } catch (ClassCastException e) {
            throw new RuntimeException("task class must implements TaskHunter!", e);
        }
        SaultFutureTask ftask = new SaultFutureTask(taskHunter);
        execute(ftask);
        return ftask;
    }


    private static final class SaultFutureTask extends FutureTask<TaskHunter>
            implements Comparable<SaultFutureTask> {
        private final TaskHunter hunter;

        SaultFutureTask(TaskHunter hunter) {
            super(hunter, null);
            this.hunter = hunter;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(SaultFutureTask other) {
            Priority p1 = hunter.getPriority();
            Priority p2 = other.hunter.getPriority();

            // High-priority requests are "lesser" so they are sorted to the front.
            // Equal priorities are sorted by sequence number to provide FIFO ordering.
            return (p1 == p2 ? hunter.getSequence() - other.hunter.getSequence() : p2.ordinal() - p1.ordinal());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SaultFutureTask that = (SaultFutureTask) o;

            return hunter != null ? hunter.equals(that.hunter) : that.hunter == null;

        }

        @Override
        public int hashCode() {
            return hunter != null ? hunter.hashCode() : 0;
        }
    }
}
