package com.bestxty.sault.internal.dispatcher;

import android.net.NetworkInfo;
import android.os.Handler;

import com.bestxty.sault.internal.NetworkStatusProvider;
import com.bestxty.sault.internal.dispatcher.handler.InternalEventDispatcherHandler;
import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.task.SaultTask;

import javax.inject.Inject;
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
class DefaultHunterEventDispatcher implements HunterEventDispatcher,
        TaskRequestEventDispatcher, NetworkEventDispatcher, NetworkStatusProvider.NetworkStatusListener {

    private static final String TAG = "DefaultHunterEventDispatcher";

    private final Handler hunterHandler;
    private final NetworkStatusProvider networkStatusProvider;

    @Inject
    DefaultHunterEventDispatcher(InternalEventDispatcherHandler internalEventDispatcherHandler,
                                 NetworkStatusProvider networkStatusProvider) {
        this.hunterHandler = internalEventDispatcherHandler;
        this.networkStatusProvider = networkStatusProvider;
        this.networkStatusProvider.addNetworkStatusListener(this);
    }

    @Override
    public void networkChange(NetworkInfo networkInfo) {
        dispatchNetworkChange(networkInfo);
    }

    @Override
    public void airplaneModeChange(boolean airplaneMode) {
        dispatchAirplaneModeChange(airplaneMode);
    }

    @Override
    public void dispatchAirplaneModeChange(boolean airplaneMode) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(AIRPLANE_MODE_CHANGE, airplaneMode));
    }

    @Override
    public void dispatchNetworkChange(NetworkInfo networkInfo) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(NETWORK_CHANGE, networkInfo));
    }

    @Override
    public void dispatchHunterStart(TaskHunter hunter) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_START, hunter));
    }

    @Override
    public void dispatchHunterRetry(TaskHunter hunter) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_RETRY, hunter));
    }

    @Override
    public void dispatchHunterException(TaskHunter hunter) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_EXCEPTION, hunter));
    }

    @Override
    public void dispatchHunterFinish(TaskHunter hunter) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_FINISH, hunter));
    }

    @Override
    public void dispatchHunterFailed(TaskHunter hunter) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_FAILED, hunter));
    }


    @Override
    public void dispatchSaultTaskSubmitRequest(SaultTask task) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_SUBMIT_REQUEST, task));
    }

    @Override
    public void dispatchSaultTaskPauseRequest(SaultTask task) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_PAUSE_REQUEST, task));
    }

    @Override
    public void dispatchSaultTaskResumeRequest(SaultTask task) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_RESUME_REQUEST, task));
    }

    @Override
    public void dispatchSaultTaskCancelRequest(SaultTask task) {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_CANCEL_REQUEST, task));
    }


    @Override
    public void shutdown() {
        networkStatusProvider.removeNetworkStatusListener(this);
        if (hunterHandler == null) {
            return;
        }
        hunterHandler.removeCallbacksAndMessages(null);
        if (hunterHandler.getLooper() != null) {
            log(TAG, "internal event dispatcher looper is null.");
            return;
        }
        hunterHandler.getLooper().quit();
    }
}
