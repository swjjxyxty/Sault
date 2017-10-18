package com.bestxty.sault.internal.handler;

import com.bestxty.sault.SaultException;
import com.bestxty.sault.internal.task.ExceptionSaultTask;
import com.bestxty.sault.internal.task.SaultTask;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
public class DefaultSaultTaskEventHandler implements SaultTaskEventHandler {

    @Inject
    public DefaultSaultTaskEventHandler() {
    }

    @Override
    public void handleSaultTaskStart(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        callback.onEvent(task.getTag(), com.bestxty.sault.Callback.EVENT_START);
    }

    @Override
    public void handleSaultTaskPause(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        callback.onEvent(task.getTag(), com.bestxty.sault.Callback.EVENT_PAUSE);
    }

    @Override
    public void handleSaultTaskResume(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        callback.onEvent(task.getTag(), com.bestxty.sault.Callback.EVENT_RESUME);
    }

    @Override
    public void handleSaultTaskCancel(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        callback.onEvent(task.getTag(), com.bestxty.sault.Callback.EVENT_CANCEL);
    }

    @Override
    public void handleSaultTaskComplete(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        callback.onEvent(task.getTag(), com.bestxty.sault.Callback.EVENT_COMPLETE);
    }

    @Override
    public void handleSaultTaskProgress(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        SaultTask.Progress progress = task.getProgress();
        callback.onProgress(task.getTag(), progress.getTotalSize(), progress.getFinishedSize());
    }

    @Override
    public void handleSaultTaskException(SaultTask task) {
        com.bestxty.sault.Callback callback = task.getCallback();
        if (callback == null) {
            return;
        }
        if (task instanceof ExceptionSaultTask) {
            Exception exception = ((ExceptionSaultTask) task).getException();
            callback.onError(new SaultException(exception));
        }
    }
}
