package com.bestxty.sault.event;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public interface EventCallbackExecutor {

    void execute(Runnable task);

    void shutdown();

}
