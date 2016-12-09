package com.bestxty.dl;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public interface Callback {

    int EVENT_START = 1;
    int EVENT_PAUSE = 2;
    int EVENT_RESUME = 3;
    int EVENT_CANCEL = 4;
    int EVENT_COMPLETE = 5;

    void onEvent(Object tag, int event);

    void onProgress(Object tag, long totalSize, long finishedSize);

    void onComplete(Object tag, String path);

    void onError(DownloadException exception);
}
