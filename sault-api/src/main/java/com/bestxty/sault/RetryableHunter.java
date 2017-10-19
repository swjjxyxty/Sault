package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public interface RetryableHunter extends CancelableHunter {

    int getRetryCount();

}
