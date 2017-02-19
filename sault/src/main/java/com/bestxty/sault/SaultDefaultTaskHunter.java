package com.bestxty.sault;

import android.net.NetworkInfo;

import com.bestxty.sault.Utils.ProgressInformer;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
class SaultDefaultTaskHunter extends AbstractSaultTaskHunter {

    private final long DEFAULT_START_POSITION = 0L;
    private final long DEFAULT_END_POSITION = 0L;

    private long startPosition = DEFAULT_START_POSITION;
    private long endPosition = DEFAULT_END_POSITION;

    private HunterStatusListener listener;
    private ProgressInformer progressInformer;

    SaultDefaultTaskHunter(Sault sault,
                           Dispatcher dispatcher,
                           Task task,
                           Downloader downloader) {
        super(sault, dispatcher, task, downloader);
        progressInformer = new ProgressInformer(task.getTag(), task.getCallback());

        if (isNeedResume()) {
            startPosition = task.finishedSize;
        }


    }


    SaultDefaultTaskHunter(Sault sault,
                           Dispatcher dispatcher,
                           Task task,
                           Downloader downloader,
                           HunterStatusListener listener,
                           long startPosition,
                           long endPosition) {
        super(sault, dispatcher, task, downloader);
        this.listener = listener;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    @Override
    long getStartPosition() {
        return startPosition;
    }

    @Override
    long getEndPosition() {
        return endPosition;
    }

    @Override
    void onProgress(int length) {

        if (listener != null) {
            listener.onProgress(length);
            return;
        }

        task.finishedSize += length;
        progressInformer.finishedSize = task.finishedSize;
        dispatcher.dispatchProgress(progressInformer);


    }

    @Override
    void onStart(long totalSize) {
        if (!isNeedResume()) {
            task.totalSize = totalSize;
        }

        progressInformer.totalSize = task.totalSize;
    }

    @Override
    void onFinish() {
        if (listener != null) {
            listener.onFinish(this);
            return;
        }

        dispatcher.dispatchComplete(this);

        progressInformer = null;
    }

    @Override
    void onError(Exception exception) {
        exception.printStackTrace();
        dispatcher.dispatchFailed(this);
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }
}
