package com.bestxty.sault.handler;

import com.bestxty.sault.task.SaultTask;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface SaultTaskEventHandler {

    int SAULT_TASK_START = 300;
    int SAULT_TASK_PAUSE = 301;
    int SAULT_TASK_RESUME = 302;
    int SAULT_TASK_CANCEL = 303;
    int SAULT_TASK_COMPLETE = 304;
    int SAULT_TASK_PROGRESS = 305;
    int SAULT_TASK_EXCEPTION = 306;

    void handleSaultTaskStart(SaultTask task);

    void handleSaultTaskPause(SaultTask task);

    void handleSaultTaskResume(SaultTask task);

    void handleSaultTaskCancel(SaultTask task);

    void handleSaultTaskComplete(SaultTask task);

    void handleSaultTaskProgress(SaultTask task);

    void handleSaultTaskException(SaultTask task);
}
