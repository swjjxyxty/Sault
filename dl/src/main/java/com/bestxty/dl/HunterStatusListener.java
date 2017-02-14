package com.bestxty.dl;

/**
 * @author xty
 *         Created by xty on 2017/2/14.
 */
interface HunterStatusListener {

    void onProgress(long progress);

    void onFinish(SaultTaskHunter.InternalTaskHunter hunter);
}
