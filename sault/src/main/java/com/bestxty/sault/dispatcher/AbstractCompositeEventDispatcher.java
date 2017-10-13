package com.bestxty.sault.dispatcher;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public abstract class AbstractCompositeEventDispatcher implements SaultTaskEventDispatcher,
        TaskRequestEventDispatcher,
        HunterEventDispatcher {

    public abstract void shutdown();
}
