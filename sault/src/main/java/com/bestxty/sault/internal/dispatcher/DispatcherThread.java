package com.bestxty.sault.internal.dispatcher;

import android.os.HandlerThread;

import javax.inject.Inject;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.bestxty.sault.internal.Utils.DISPATCHER_THREAD_NAME;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
class DispatcherThread extends HandlerThread {
    @Inject
    DispatcherThread() {
        super(DISPATCHER_THREAD_NAME, THREAD_PRIORITY_BACKGROUND);
    }
}
