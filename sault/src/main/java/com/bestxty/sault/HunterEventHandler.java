package com.bestxty.sault;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public interface HunterEventHandler {

    int HUNTER_START = 200;
    int HUNTER_RETRY = 204;
    int HUNTER_EXCEPTION = 205;
    int HUNTER_FINISH = 206;
    int HUNTER_FAILED = 207;


    void handleHunterStart(TaskHunter hunter);

    void handleHunterRetry(TaskHunter hunter);

    void handleHunterException(TaskHunter hunter);

    void handleHunterFinish(TaskHunter hunter);

    void handleHunterFailed(TaskHunter hunter);

}
