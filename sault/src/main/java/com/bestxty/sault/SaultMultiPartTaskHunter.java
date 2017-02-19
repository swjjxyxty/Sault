package com.bestxty.sault;

import android.net.NetworkInfo;

import com.bestxty.sault.Utils.ProgressInformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static com.bestxty.sault.Utils.THREAD_IDLE_NAME;
import static com.bestxty.sault.Utils.log;
import static java.lang.Thread.currentThread;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
class SaultMultiPartTaskHunter extends BaseSaultTaskHunter implements HunterStatusListener {


    private static final int LENGTH_PER_THREAD = 1024 * 1024 * 10;      //10M

    private List<TaskHunter> taskHunterList;

    private ProgressInformer progressInformer;

    SaultMultiPartTaskHunter(Sault sault, Dispatcher dispatcher, Task task, Downloader downloader) {
        super(sault, dispatcher, task, downloader);
        taskHunterList = new ArrayList<>();
        progressInformer = new ProgressInformer(task.getTag(), task.getCallback());
    }


    @Override
    public void onProgress(long length) {
        task.finishedSize += length;
        progressInformer.finishedSize = task.finishedSize;
        dispatcher.dispatchProgress(progressInformer);
    }


    @Override
    public void onFinish(TaskHunter hunter) {
        taskHunterList.remove(hunter);

        if (taskHunterList.isEmpty()) {
            task.endTime = System.nanoTime();
            dispatcher.dispatchComplete(this);
        }

        progressInformer = null;
    }

    private void calculateTaskCount() throws IOException {

        List<Task> subTaskList = task.getSubTaskList();
        long totalSize = downloader.fetchContentLength(task.getUri());

        task.totalSize = totalSize;
        progressInformer.totalSize = totalSize;

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
        } finally {
            currentThread().setName(THREAD_IDLE_NAME);
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
