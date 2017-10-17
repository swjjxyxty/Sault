package com.bestxty.sault.dispatcher;

import com.bestxty.sault.hunter.TaskHunter;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface HunterEventDispatcher extends EventDispatcher {

    void dispatchHunterStart(TaskHunter hunter);

    void dispatchHunterRetry(TaskHunter hunter);

    void dispatchHunterException(TaskHunter hunter);

    void dispatchHunterFinish(TaskHunter hunter);

    void dispatchHunterFailed(TaskHunter hunter);

}
