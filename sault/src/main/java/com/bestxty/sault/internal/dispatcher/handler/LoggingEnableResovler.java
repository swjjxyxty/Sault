package com.bestxty.sault.internal.dispatcher.handler;

import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.task.SaultTask;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/18.
 */

class LoggingEnableResovler {

    private boolean loggingEnabledResovled = false;

    private boolean isLoggingEnabled = false;

    private boolean resovleIsLoggingEnabled(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof SaultTask) {
            return ((SaultTask) obj).getSault().isLoggingEnabled();
        }
        if (obj instanceof TaskHunter) {
            return ((TaskHunter) obj).getSault().isLoggingEnabled();
        }
        return false;
    }

    boolean isLoggingEnabled(Object obj) {
        if (loggingEnabledResovled) {
            return isLoggingEnabled;
        }
        isLoggingEnabled = resovleIsLoggingEnabled(obj);
        loggingEnabledResovled = true;
        return isLoggingEnabled;
    }
}
