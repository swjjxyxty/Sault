package com.bestxty.dl;

import static com.bestxty.dl.DownloadThread.*;

/**
 * @author swjjx
 *         Created by swjjx on 2017/1/21. for DownloadLibrary
 */
interface DownloadThreadStatusListener {

    void onProgress(long length);

    void onFinish(DownloadThreadInfo threadInfo);

    void onInterrupt(DownloadThreadInfo threadInfo);
}
