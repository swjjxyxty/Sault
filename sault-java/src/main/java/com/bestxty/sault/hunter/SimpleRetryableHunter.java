package com.bestxty.sault.hunter;

import com.bestxty.sault.RetryableHunter;
import com.bestxty.sault.downloader.Downloader;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class SimpleRetryableHunter extends SimpleCancelableHunter implements RetryableHunter {

    protected Downloader downloader;

    public SimpleRetryableHunter(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public int getRetryCount() {
        return downloader.getRetryCount();
    }
}
