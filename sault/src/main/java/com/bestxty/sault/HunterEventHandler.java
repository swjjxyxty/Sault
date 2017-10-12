package com.bestxty.sault;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface HunterEventHandler {

    int HUNTER_START = 200;
    int HUNTER_CANCEL = 201;
    int HUNTER_RETRY = 202;
    int HUNTER_EXCEPTION = 203;
    int HUNTER_FINISH = 204;
    int HUNTER_FAILED = 205;


    void handleHunterStart(TaskHunter hunter);

    void handleHunterCancel(TaskHunter hunter);

    void handleHunterRetry(TaskHunter hunter);

    void handleHunterException(TaskHunter hunter);

    void handleHunterFinish(TaskHunter hunter);

    void handleHunterFailed(TaskHunter hunter);

}
