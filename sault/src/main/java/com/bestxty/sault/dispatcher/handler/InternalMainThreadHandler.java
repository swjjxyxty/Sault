package com.bestxty.sault.dispatcher.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bestxty.sault.handler.SaultTaskEventHandler;
import com.bestxty.sault.task.SaultTask;

import javax.inject.Inject;
import javax.inject.Named;
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
public class InternalMainThreadHandler extends Handler {


    private final SaultTaskEventHandler saultTaskEventHandler;

    @Inject
    public InternalMainThreadHandler(@Named("mainLooper") Looper looper,
                                     SaultTaskEventHandler saultTaskEventHandler) {
        super(looper);
        this.saultTaskEventHandler = saultTaskEventHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SAULT_TASK_START:
                saultTaskEventHandler.handleSaultTaskStart(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_PAUSE:
                saultTaskEventHandler.handleSaultTaskPause(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_RESUME:
                saultTaskEventHandler.handleSaultTaskResume(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_CANCEL:
                saultTaskEventHandler.handleSaultTaskCancel(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_COMPLETE:
                saultTaskEventHandler.handleSaultTaskComplete(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_PROGRESS:
                saultTaskEventHandler.handleSaultTaskProgress(((SaultTask) msg.obj));
                break;
            case SAULT_TASK_EXCEPTION:
                saultTaskEventHandler.handleSaultTaskException(((SaultTask) msg.obj));
                break;
        }
    }
}
