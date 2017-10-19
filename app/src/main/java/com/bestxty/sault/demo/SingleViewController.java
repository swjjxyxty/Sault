package com.bestxty.sault.demo;

import android.util.Log;

import com.bestxty.sault.Callback;
import com.bestxty.sault.Sault;
import com.bestxty.sault.SaultException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */

public final class SingleViewController implements Callback {

    private SingleTaskView singleTaskView;

    private Sault sault;

    private Object tag;

    private AtomicBoolean finish = new AtomicBoolean(true);

    public void attachView(SingleTaskView singleTaskView) {
        this.singleTaskView = singleTaskView;
        sault = Sault.getInstance(singleTaskView.getContext());
    }

    public void start(String url) {
        if (tag != null && !finish.get()) {
            return;
        }
        tag = sault.load(url)
                .tag("test-task")
                .listener(this)
                .breakPointEnabled(true)
                .multiThreadEnabled(true)
                .priority(Sault.Priority.HIGH)
                .go();
    }


    public void pause() {
        if (tag == null) return;
        sault.pause(tag);
    }

    public void cancel() {
        if (tag == null) return;
        sault.cancel(tag);
    }

    public void resume() {
        if (tag == null) return;
        sault.resume(tag);
    }

    @Override
    public void onError(SaultException exception) {
        singleTaskView.showError(exception.getMessage());
        singleTaskView.enableStartBtn();
        exception.printStackTrace();
    }

    @Override
    public void onEvent(Object tag, int event) {
        switch (event) {
            case EVENT_START:
            case EVENT_RESUME:
                singleTaskView.enablePauseAndCancelBtn();
                break;
            case EVENT_PAUSE:
                singleTaskView.enableResumeAndCancelBtn();
                break;
            case EVENT_CANCEL:
                singleTaskView.enableStartBtn();
                break;
        }
        switch (event) {
            case EVENT_START:
                finish.set(false);
                singleTaskView.showStatus("Running");
                break;
            case EVENT_PAUSE:
                singleTaskView.showStatus("Paused");
                break;
            case EVENT_RESUME:
                singleTaskView.showStatus("Running");
                break;
            case EVENT_CANCEL:
                singleTaskView.showStatus("Canceled");
                finish.set(true);
                singleTaskView.clearProgress();
                break;
            case EVENT_COMPLETE:
                singleTaskView.showStatus("Complete");
                break;
        }

    }

    @Override
    public void onProgress(Object tag, long totalSize, long finishedSize) {
        singleTaskView.showProgress(totalSize, finishedSize);
    }

    @Override
    public void onComplete(Object tag, String path) {
        this.tag = null;
        finish.set(true);
        singleTaskView.enableStartBtn();
        Log.d("SingleViewController", path);
    }
}
