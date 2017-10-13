package com.bestxty.sault.hunter;

import android.net.NetworkInfo;

import com.bestxty.sault.Sault;
import com.bestxty.sault.task.SaultTask;

import java.util.concurrent.Future;

import static com.bestxty.sault.Sault.Priority;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/21. for DownloadLibrary
 */
public interface TaskHunter extends Runnable {

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
