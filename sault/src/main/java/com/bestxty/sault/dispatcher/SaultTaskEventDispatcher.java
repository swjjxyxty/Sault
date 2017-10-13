package com.bestxty.sault.dispatcher;

import com.bestxty.sault.task.SaultTask;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface SaultTaskEventDispatcher {

    void dispatchSaultTaskStart(SaultTask task);

    void dispatchSaultTaskPause(SaultTask task);

    void dispatchSaultTaskResume(SaultTask task);

    void dispatchSaultTaskCancel(SaultTask task);

    void dispatchSaultTaskComplete(SaultTask task);

    void dispatchSaultTaskProgress(SaultTask task);

    void dispatchSaultTaskException(SaultTask task);
}
