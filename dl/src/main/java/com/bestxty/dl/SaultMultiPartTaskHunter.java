package com.bestxty.dl;

import android.net.NetworkInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static com.bestxty.dl.Utils.THREAD_IDLE_NAME;
import static com.bestxty.dl.Utils.log;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
class SaultMultiPartTaskHunter extends BaseSaultTaskHunter implements HunterStatusListener {


    private static final int LENGTH_PER_THREAD = 1024 * 1024 * 10;      //10M

    private List<TaskHunter> taskHunterList;

    SaultMultiPartTaskHunter(Sault sault, Dispatcher dispatcher, Task task, Downloader downloader) {
        super(sault, dispatcher, task, downloader);
        taskHunterList = new ArrayList<>();
    }


    @Override
    public void onProgress(long length) {
        task.finishedSize += length;
        Utils.ProgressInformer progress = new Utils.ProgressInformer(task.getTag(), task.getCallback());

        progress.totalSize = task.totalSize;

        progress.finishedSize = task.finishedSize;
        dispatcher.dispatchProgress(progress);
    }

//    @Override
//    public void onFinish(SaultTaskHunter.InternalTaskHunter hunter) {
//
//    }

    @Override
    public void onFinish(TaskHunter hunter) {
        taskHunterList.remove(hunter);

        if (taskHunterList.isEmpty()) {
            task.endTime = System.nanoTime();
            dispatcher.dispatchComplete(this);
        }
    }

    private void calculateTaskCount() throws IOException {

        List<Task> subTaskList = task.getSubTaskList();
        long totalSize = downloader.fetchContentLength(task.getUri());

        task.totalSize = totalSize;

        long threadSize;
        long threadLength = LENGTH_PER_THREAD;
        if (totalSize <= LENGTH_PER_THREAD) {
            threadSize = 2;
            threadLength = totalSize / threadSize;
        } else {
            threadSize = totalSize / LENGTH_PER_THREAD;
        }
        log(threadSize + "------x");
        long remainder = totalSize % threadLength;
        for (int i = 0; i < threadSize; i++) {
            long start = i * threadLength;
            long end = start + threadLength - 1;
            if (i == threadSize - 1) {
                end = start + threadLength + remainder - 1;
            }
            subTaskList.add(new Task(task.getSault(), task.getKey(), task.getUri(),
                    task.getTarget(), task.getTag(), task.getPriority(), task.getCallback(),
                    task.isMultiThreadEnabled(), task.isBreakPointEnabled(), start, end));
        }
    }

    @Override
    public void run() {
        updateThreadName();
        try {
            if (!isNeedResume()) {
                calculateTaskCount();
            }

            for (Task subTask : task.getSubTaskList()) {
                log(subTask.toString());
                if (subTask.isDone()) {
                    log("subtask is done break. " + subTask.toString());
                    continue;
                }
                TaskHunter taskHunter = new SaultDefaultTaskHunter(task.getSault(), dispatcher, task,
                        downloader, this, subTask.getStartPosition(), subTask.getEndPosition());

                taskHunterList.add(taskHunter);
                Future future = dispatcher.submit(taskHunter);
                taskHunter.setFuture(future);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setException(e);
        }finally {
            Thread.currentThread().setName(THREAD_IDLE_NAME);
        }
    }

    @Override
    public boolean cancel() {
        for (TaskHunter taskHunter : taskHunterList) {
            if (!taskHunter.cancel()) return false;
        }
        return super.cancel();
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }
}
