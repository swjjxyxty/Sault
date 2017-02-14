package com.bestxty.dl;

/**
 * download task callback.
 *
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public interface Callback {

    /**
     * task download start
     */
    int EVENT_START = 1;
    /**
     * task download pause
     */
    int EVENT_PAUSE = 2;
    /**
     * task download resume
     */
    int EVENT_RESUME = 3;
    /**
     * task download cancel
     */
    int EVENT_CANCEL = 4;
    /**
     * task download complete
     */
    int EVENT_COMPLETE = 5;

    /**
     * task event callback.
     *
     * @param tag   task's tag.{@link Task#getTag()}
     * @param event task's event. {@link Callback#EVENT_START},
     *              {@link Callback#EVENT_PAUSE},
     *              {@link Callback#EVENT_RESUME},
     *              {@link Callback#EVENT_CANCEL},
     *              {@link Callback#EVENT_COMPLETE}
     */
    void onEvent(Object tag, int event);

    /**
     * task download progress
     *
     * @param tag          task's tag.
     * @param totalSize    task's total size.
     * @param finishedSize task's finished size.
     */
    void onProgress(Object tag, long totalSize, long finishedSize);

    /**
     * task download complete callback.
     *
     * @param tag  task's tag.{@link Task#getTag()}
     * @param path task's file save path.{@link Task#getTarget()}
     */
    void onComplete(Object tag, String path);

    /**
     * task download error.
     *
     * @param exception exception info.{@link SaultException}
     */
    void onError(SaultException exception);
}
