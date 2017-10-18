package com.bestxty.sault.internal.handler;


import com.bestxty.sault.internal.task.SaultTask;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface TaskRequestEventHandler {

    int TASK_SUBMIT_REQUEST = 100;
    int TASK_PAUSE_REQUEST = 101;
    int TASK_RESUME_REQUEST = 102;
    int TASK_CANCEL_REQUEST = 103;

    void handleSaultTaskSubmitRequest(SaultTask task);

    void handleSaultTaskPauseRequest(SaultTask task);

    void handleSaultTaskResumeRequest(SaultTask task);

    void handleSaultTaskCancelRequest(SaultTask task);
}
