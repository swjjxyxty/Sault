package com.bestxty.dl;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.bestxty.dl.Sault.Priority;
import static com.bestxty.dl.Utils.DownloadThreadFactory;
import static com.bestxty.dl.Utils.log;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
class SaultExecutorService extends ThreadPoolExecutor {

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_THREAD_COUNT = CORES + 1;


    private static final int DEFAULT_NETWORK_WIFI_THREAD_COUNT = DEFAULT_THREAD_COUNT;
    private static final int DEFAULT_NETWORK_4G_THREAD_COUNT = DEFAULT_NETWORK_WIFI_THREAD_COUNT;
    private static final int DEFAULT_NETWORK_3G_THREAD_COUNT = DEFAULT_NETWORK_4G_THREAD_COUNT - 1;
    // FIXME: 2017/2/8 thread count !=cores. cores maybe <=2
    private static final int DEFAULT_NETWORK_2G_THREAD_COUNT = DEFAULT_NETWORK_3G_THREAD_COUNT - 1;

    private int threadCount = DEFAULT_THREAD_COUNT;
    private int networkWIFIThreadCount = DEFAULT_NETWORK_WIFI_THREAD_COUNT;  //wifi
    private int networkLTEThreadCount = DEFAULT_NETWORK_4G_THREAD_COUNT;     //4G
    private int networkCDMAThreadCount = DEFAULT_NETWORK_3G_THREAD_COUNT;    //3G
    private int networkGPRSThreadCount = DEFAULT_NETWORK_2G_THREAD_COUNT;    //2G

    SaultExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MICROSECONDS,
                new PriorityBlockingQueue<Runnable>(), new DownloadThreadFactory());
        log("cores="+CORES);
        log("DEFAULT_THREAD_COUNT="+DEFAULT_THREAD_COUNT);
        log("DEFAULT_NETWORK_4G_THREAD_COUNT="+DEFAULT_NETWORK_4G_THREAD_COUNT);
        log("DEFAULT_NETWORK_3G_THREAD_COUNT="+DEFAULT_NETWORK_3G_THREAD_COUNT);
        log("DEFAULT_NETWORK_2G_THREAD_COUNT="+DEFAULT_NETWORK_2G_THREAD_COUNT);
    }


    void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    void setNetworkWIFIThreadCount(int networkWIFIThreadCount) {
        this.networkWIFIThreadCount = networkWIFIThreadCount;
    }

    void setNetworkLTEThreadCount(int networkLTEThreadCount) {
        this.networkLTEThreadCount = networkLTEThreadCount;
    }

    void setNetworkCDMAThreadCount(int networkCDMAThreadCount) {
        this.networkCDMAThreadCount = networkCDMAThreadCount;
    }

    void setNetworkGPRSThreadCount(int networkGPRSThreadCount) {
        this.networkGPRSThreadCount = networkGPRSThreadCount;
    }

    void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            internalSetThreadCount(threadCount);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                internalSetThreadCount(networkWIFIThreadCount);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        internalSetThreadCount(networkLTEThreadCount);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        internalSetThreadCount(networkCDMAThreadCount);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        internalSetThreadCount(networkGPRSThreadCount);
                        break;
                    default:
                        internalSetThreadCount(threadCount);
                }
                break;
            default:
                internalSetThreadCount(threadCount);
        }
    }

    private void internalSetThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

    @Override
    public Future<?> submit(Runnable task) {
        DownloadFutureTask ftask = new DownloadFutureTask((TaskHunter) task);
        execute(ftask);
        return ftask;
    }


    private static final class DownloadFutureTask extends FutureTask<TaskHunter>
            implements Comparable<DownloadFutureTask> {
        private final TaskHunter hunter;

        DownloadFutureTask(TaskHunter hunter) {
            super(hunter, null);
            this.hunter = hunter;
        }

        @Override
        public int compareTo(@NonNull DownloadFutureTask other) {
            Priority p1 = hunter.getPriority();
            Priority p2 = other.hunter.getPriority();

            // High-priority requests are "lesser" so they are sorted to the front.
            // Equal priorities are sorted by sequence number to provide FIFO ordering.
            return (p1 == p2 ? hunter.getSequence() - other.hunter.getSequence() : p2.ordinal() - p1.ordinal());
        }
    }
}
