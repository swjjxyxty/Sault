package com.bestxty.sault.hunter;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Downloader.ContentLengthException;
import com.bestxty.sault.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.task.PartedSaultTask;
import com.bestxty.sault.task.SaultTask;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static com.bestxty.sault.Utils.closeQuietly;
import static com.bestxty.sault.Utils.createTargetFile;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public class PartingSaultTaskHunter extends AbstractTaskHunter {


    private static final int LENGTH_PER_THREAD = 1024 * 1024 * 10;      //10M

    private Exception exception;
    private HunterEventDispatcher eventDispatcher;
    private SaultTaskEventDispatcher taskEventDispatcher;
    private TaskRequestEventDispatcher taskRequestEventDispatcher;

    public PartingSaultTaskHunter(SaultTask task, Downloader downloader,
                                  HunterEventDispatcher eventDispatcher,
                                  SaultTaskEventDispatcher taskEventDispatcher,
                                  TaskRequestEventDispatcher taskRequestEventDispatcher) {
        super(task, downloader);
        this.eventDispatcher = eventDispatcher;
        this.taskEventDispatcher = taskEventDispatcher;
        this.taskRequestEventDispatcher = taskRequestEventDispatcher;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    void hunter() {
        try {
            eventDispatcher.dispatchHunterStart(this);
            SaultTask task = getTask();
            task.setStartTime(System.nanoTime());
            Downloader downloader = getDownloader();
            long totalSize = downloader.fetchContentLength(task.getUri());

            if (totalSize == 0) {
                throw new ContentLengthException("Received response with 0 content-length header.");
            }

            task.setTotalSize(totalSize);
            createTargetFile(task.getTarget());
            RandomAccessFile targetFile = new RandomAccessFile(task.getTarget(), "rw");
            targetFile.setLength(totalSize);
            closeQuietly(targetFile);

            long threadSize;
            long threadLength = LENGTH_PER_THREAD;
            if (totalSize <= LENGTH_PER_THREAD) {
                threadSize = 2;
                threadLength = totalSize / threadSize;
            } else {
                threadSize = totalSize / LENGTH_PER_THREAD;
            }
            long remainder = totalSize % threadLength;
            List<SaultTask> partedTasks = new ArrayList<>();
            for (int i = 0; i < threadSize; i++) {
                long start = i * threadLength;
                long end = start + threadLength - 1;
                if (i == threadSize - 1) {
                    end = start + threadLength + remainder - 1;
                }
                partedTasks.add(new PartedSaultTask(task,
                        taskEventDispatcher, start, end));
            }
            for (SaultTask partedTask : partedTasks) {
                taskRequestEventDispatcher.dispatchSaultTaskSubmitRequest(partedTask);
            }
            eventDispatcher.dispatchHunterFinish(this);
        } catch (Exception e) {
            exception = e;
            eventDispatcher.dispatchHunterException(this);
        }
    }
}
