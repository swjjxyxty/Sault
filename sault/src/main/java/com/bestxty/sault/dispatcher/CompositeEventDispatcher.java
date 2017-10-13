package com.bestxty.sault.dispatcher;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.bestxty.sault.handler.HunterEventHandler;
import com.bestxty.sault.handler.MainThreadHandler;
import com.bestxty.sault.handler.TaskRequestEventHandler;
import com.bestxty.sault.hunter.TaskHunter;
import com.bestxty.sault.task.SaultTask;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.bestxty.sault.Utils.DISPATCHER_THREAD_NAME;
import static com.bestxty.sault.handler.HunterEventHandler.HUNTER_EXCEPTION;
import static com.bestxty.sault.handler.HunterEventHandler.HUNTER_FAILED;
import static com.bestxty.sault.handler.HunterEventHandler.HUNTER_FINISH;
import static com.bestxty.sault.handler.HunterEventHandler.HUNTER_RETRY;
import static com.bestxty.sault.handler.HunterEventHandler.HUNTER_START;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_CANCEL;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_COMPLETE;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_EXCEPTION;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_PAUSE;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_PROGRESS;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_RESUME;
import static com.bestxty.sault.handler.SaultTaskEventHandler.SAULT_TASK_START;
import static com.bestxty.sault.handler.TaskRequestEventHandler.TASK_CANCEL_REQUEST;
import static com.bestxty.sault.handler.TaskRequestEventHandler.TASK_PAUSE_REQUEST;
import static com.bestxty.sault.handler.TaskRequestEventHandler.TASK_RESUME_REQUEST;
import static com.bestxty.sault.handler.TaskRequestEventHandler.TASK_SUBMIT_REQUEST;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */


public class CompositeEventDispatcher extends AbstractCompositeEventDispatcher {

    private HunterEventDispatcherHandler hunterHandler;
    private DispatcherThread dispatcherThread;
    private MainThreadHandler mainThreadHandler;

    public CompositeEventDispatcher(MainThreadHandler mainThreadHandler) {
        this.mainThreadHandler = mainThreadHandler;
        this.dispatcherThread = new DispatcherThread();
        dispatcherThread.start();
        hunterHandler
                = new HunterEventDispatcherHandler(dispatcherThread.getLooper());
    }

    public void setHunterEventHandler(HunterEventHandler hunterEventHandler) {
        hunterHandler.setHunterEventHandler(hunterEventHandler);
    }

    public void setTaskRequestEventHandler(TaskRequestEventHandler taskRequestEventHandler) {
        hunterHandler.setTaskRequestEventHandler(taskRequestEventHandler);
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
        hunterHandler.removeCallbacksAndMessages(null);
        dispatcherThread.quit();
    }


    private static class HunterEventDispatcherHandler extends Handler {

        private TaskRequestEventHandler taskRequestEventHandler;
        private HunterEventHandler hunterEventHandler;

        public HunterEventDispatcherHandler(Looper looper) {
            super(looper);
        }

        public void setHunterEventHandler(HunterEventHandler hunterEventHandler) {
            this.hunterEventHandler = hunterEventHandler;
        }

        public void setTaskRequestEventHandler(TaskRequestEventHandler taskRequestEventHandler) {
            this.taskRequestEventHandler = taskRequestEventHandler;
        }

        @Override
        public void handleMessage(Message msg) {
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
            }
        }
    }

    private static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super(DISPATCHER_THREAD_NAME, THREAD_PRIORITY_BACKGROUND);
        }
    }
}
