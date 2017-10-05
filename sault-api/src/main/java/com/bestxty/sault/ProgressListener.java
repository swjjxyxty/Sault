package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public interface ProgressListener {

    void onProgress(Object tag, long totalSize, long finishedSize);

    void onComplete(Object tag, String path);

}
