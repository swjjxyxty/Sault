package com.bestxty.sault.hunter;

import com.bestxty.sault.RetryableHunter;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class SimpleRetryableHunter extends SimpleCancelableHunter implements RetryableHunter {

    @Override
    public int getRetryCount() {
        return 0;
    }
}
