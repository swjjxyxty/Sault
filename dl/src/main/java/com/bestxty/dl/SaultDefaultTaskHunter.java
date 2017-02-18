package com.bestxty.dl;

import android.net.NetworkInfo;

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

    SaultDefaultTaskHunter(Sault sault,
                           Dispatcher dispatcher,
                           Task task,
                           Downloader downloader) {
        super(sault, dispatcher, task, downloader);
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
        Utils.ProgressInformer progress = new Utils.ProgressInformer(task.getTag(), task.getCallback());
        progress.totalSize = task.totalSize;
        progress.finishedSize = task.finishedSize;
        dispatcher.dispatchProgress(progress);


    }

    @Override
    void onStart() {

    }

    @Override
    void onFinish() {
        listener.onFinish(this);
    }

    @Override
    void onError(Exception exception) {

    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }
}
