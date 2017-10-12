package com.bestxty.sault;

import android.net.NetworkInfo;

import java.util.concurrent.Future;

import static com.bestxty.sault.Sault.Priority;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/21. for DownloadLibrary
 */
interface TaskHunter extends Runnable {

    Sault getSault();

    SaultTask getTask();

    Exception getException();

    Priority getPriority();

    int getSequence();

    boolean cancel();

    boolean isCancelled();

    boolean isNeedResume();

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info);

    void setFuture(Future<?> future);
}
