package com.bestxty.sault.internal.dispatcher.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bestxty.sault.internal.handler.SaultTaskEventHandler;
import com.bestxty.sault.internal.task.SaultTask;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.bestxty.sault.internal.Utils.log;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_CANCEL;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_COMPLETE;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_EXCEPTION;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_PAUSE;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_PROGRESS;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_RESUME;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_START;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
public class InternalMainThreadHandler extends Handler {

    private static final String TAG = "InternalMainThreadHandler";

    private final SaultTaskEventHandler saultTaskEventHandler;

    private final LoggingEnableResovler loggingEnableResovler;

    @Inject
    InternalMainThreadHandler(@Named("mainLooper") Looper looper,
                              SaultTaskEventHandler saultTaskEventHandler) {
        super(looper);
        this.saultTaskEventHandler = saultTaskEventHandler;
        this.loggingEnableResovler = new LoggingEnableResovler();
    }


    @Override
    public void handleMessage(Message msg) {
        if (loggingEnableResovler.isLoggingEnabled(msg.obj)) {
            log(TAG, "dispatch task event, event = " + msg.what);
        }
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
            default:
                log(TAG, "unknown msg type : " + msg.what);
                break;
        }
    }
}
