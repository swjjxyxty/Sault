package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2017/2/14.
 */
interface HunterStatusListener {

    void onProgress(long progress);

    void onFinish(TaskHunter hunter);
}
