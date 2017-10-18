package com.bestxty.sault.dispatcher;

import android.os.Handler;

import com.bestxty.sault.dispatcher.handler.InternalMainThreadHandler;
import com.bestxty.sault.task.SaultTask;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_CANCEL;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_COMPLETE;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_EXCEPTION;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_PAUSE;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_PROGRESS;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_RESUME;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_START;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
public class DefaultSaultTaskEventDispatcher implements SaultTaskEventDispatcher {
    private final Handler mainThreadHandler;

    @Inject
    DefaultSaultTaskEventDispatcher(InternalMainThreadHandler mainThreadHandler) {
        this.mainThreadHandler = mainThreadHandler;
    }

    @Override
    public void shutdown() {
        if (mainThreadHandler != null)
            mainThreadHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void dispatchSaultTaskStart(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_START, task));
    }

    @Override
    public void dispatchSaultTaskPause(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_PAUSE, task));
    }

    @Override
    public void dispatchSaultTaskResume(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_RESUME, task));
    }

    @Override
    public void dispatchSaultTaskCancel(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_CANCEL, task));
    }

    @Override
    public void dispatchSaultTaskComplete(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_COMPLETE, task));
    }

    @Override
    public void dispatchSaultTaskProgress(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_PROGRESS, task));
    }

    @Override
    public void dispatchSaultTaskException(SaultTask task) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_EXCEPTION, task));
    }

}
