package com.bestxty.sault;

import java.util.concurrent.Future;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public interface CancelableHunter extends Hunter {

    void setFuture(Future<?> future);

    boolean isDone();

    boolean isCancelled();

    boolean cancel();
}
