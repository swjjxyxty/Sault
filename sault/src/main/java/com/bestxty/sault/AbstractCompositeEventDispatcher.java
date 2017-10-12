package com.bestxty.sault;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

abstract class AbstractCompositeEventDispatcher implements SaultTaskEventDispatcher,
        TaskRequestEventDispatcher,
        HunterEventDispatcher {

    abstract void shutdown();
}
