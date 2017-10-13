package com.bestxty.sault;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface HunterEventDispatcher {

    void dispatchHunterStart(TaskHunter hunter);

    void dispatchHunterRetry(TaskHunter hunter);

    void dispatchHunterException(TaskHunter hunter);

    void dispatchHunterFinish(TaskHunter hunter);

    void dispatchHunterFailed(TaskHunter hunter);

}
