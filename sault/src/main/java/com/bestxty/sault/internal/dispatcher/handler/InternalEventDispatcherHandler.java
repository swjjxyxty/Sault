package com.bestxty.sault.internal.dispatcher.handler;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bestxty.sault.internal.handler.HunterEventHandler;
import com.bestxty.sault.internal.handler.NetworkEventHandler;
import com.bestxty.sault.internal.handler.TaskRequestEventHandler;
import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.task.SaultTask;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.bestxty.sault.internal.Utils.log;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_EXCEPTION;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_FAILED;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_FINISH;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_RETRY;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_START;
import static com.bestxty.sault.internal.handler.NetworkEventHandler.AIRPLANE_MODE_CHANGE;
import static com.bestxty.sault.internal.handler.NetworkEventHandler.NETWORK_CHANGE;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_CANCEL_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_PAUSE_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_RESUME_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_SUBMIT_REQUEST;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
public class InternalEventDispatcherHandler extends Handler {

    private static final String TAG = "InternalEventDispatcherHandler";

    private final TaskRequestEventHandler taskRequestEventHandler;
    private final HunterEventHandler hunterEventHandler;
    private final NetworkEventHandler networkEventHandler;
    private final LoggingEnableResovler loggingEnableResovler;

    @Inject
    public InternalEventDispatcherHandler(@Named("internalLooper") Looper looper,
                                          TaskRequestEventHandler taskRequestEventHandler,
                                          HunterEventHandler hunterEventHandler,
                                          NetworkEventHandler networkEventHandler) {
        super(looper);
        this.taskRequestEventHandler = taskRequestEventHandler;
        this.hunterEventHandler = hunterEventHandler;
        this.networkEventHandler = networkEventHandler;
        this.loggingEnableResovler = new LoggingEnableResovler();
    }

    @Override
    public void handleMessage(Message msg) {
        if (loggingEnableResovler.isLoggingEnabled(msg.obj)) {
            log(TAG, "dispatch internal event, event = " + msg.what);
        }
        switch (msg.what) {
            case TASK_SUBMIT_REQUEST:
                taskRequestEventHandler.handleSaultTaskSubmitRequest(((SaultTask) msg.obj));
                break;
            case TASK_PAUSE_REQUEST:
                taskRequestEventHandler.handleSaultTaskPauseRequest(((SaultTask) msg.obj));
                break;
            case TASK_RESUME_REQUEST:
                taskRequestEventHandler.handleSaultTaskResumeRequest(((SaultTask) msg.obj));
                break;
            case TASK_CANCEL_REQUEST:
                taskRequestEventHandler.handleSaultTaskCancelRequest(((SaultTask) msg.obj));
                break;
            case HUNTER_START:
                hunterEventHandler.handleHunterStart(((TaskHunter) msg.obj));
                break;
            case HUNTER_RETRY:
                hunterEventHandler.handleHunterRetry(((TaskHunter) msg.obj));
                break;
            case HUNTER_EXCEPTION:
                hunterEventHandler.handleHunterException(((TaskHunter) msg.obj));
                break;
            case HUNTER_FINISH:
                hunterEventHandler.handleHunterFinish(((TaskHunter) msg.obj));
                break;
            case HUNTER_FAILED:
                hunterEventHandler.handleHunterFailed(((TaskHunter) msg.obj));
                break;
            case AIRPLANE_MODE_CHANGE:
                networkEventHandler.handleAirplaneModeChange((Boolean) msg.obj);
                break;
            case NETWORK_CHANGE:
                networkEventHandler.handleNetworkChange((NetworkInfo) msg.obj);
                break;
            default:
                log(TAG, "unknown msg type : " + msg.what);
                break;
        }
    }
}
