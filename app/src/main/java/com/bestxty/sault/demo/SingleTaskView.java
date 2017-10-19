package com.bestxty.sault.demo;

import android.content.Context;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */

public interface SingleTaskView {

    Context getContext();

    void showProgress(long totalSize, long finishedSize);

    void clearProgress();

    void showError(String msg);

    void showStatus(String status);

    void enableStartBtn();

    void enablePauseAndCancelBtn();

    void enableResumeAndCancelBtn();
}
