package com.bestxty.sault;

import com.bestxty.sault.Utils.ProgressInformer;

import java.util.Locale;

import static com.bestxty.sault.Utils.log;

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
        progressInformer = ProgressInformer.create(task);

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
        this.startPosition = startPosition + task.finishedSize;
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

        task.finishedSize += length;

        if (listener != null) {
            listener.onProgress(length);
            return;
        }

        progressInformer.finishedSize = task.finishedSize;
        dispatcher.dispatchProgress(progressInformer);


    }

    @Override
    void onStart(long totalSize) {
        if (!isNeedResume()) {
            task.totalSize = totalSize;
        }

        if (listener != null) {
            return;
        }
        progressInformer.totalSize = task.totalSize;
    }

    @Override
    void onFinish() {
        if (listener != null) {
            if (getSault().isLoggingEnabled()) {
                log(String.format(Locale.US,
                        "Task finished. Task:{id=%d,finishedSize=%d,totalSize=%d,startPosition=%d,endPosition=%d}",
                        task.id, task.finishedSize, task.totalSize,
                        task.getStartPosition(), task.getEndPosition()));
            }
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

}
