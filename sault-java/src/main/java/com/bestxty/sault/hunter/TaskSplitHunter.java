package com.bestxty.sault.hunter;

import com.bestxty.sault.CompositeEventTaskWrapper;
import com.bestxty.sault.SplitHunterTask;
import com.bestxty.sault.SplitTask;
import com.bestxty.sault.Task;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.downloader.HeaderResponse;
import com.bestxty.sault.event.EventCallbackExecutor;
import com.bestxty.sault.event.task.TaskSplitEvent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.bestxty.sault.utils.IOUtils.closeQuietly;
import static com.bestxty.sault.utils.Utils.createTargetFile;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class TaskSplitHunter extends EventSupportedHunter {

    private static final long DEFAULT_PER_THREAD_LENGTH = 1024 * 1024 * 10;//10M

    private Task task;

    public TaskSplitHunter(Downloader downloader, EventCallbackExecutor eventCallbackExecutor, Task task) {
        super(downloader, task, eventCallbackExecutor);
        this.task = task;
    }

    @Override
    public void run() {
        URI uri = task.getUri();
        Map<String, String> headers = task.getHeaderMap() == null
                ? Collections.<String, String>emptyMap() : task.getHeaderMap();

        try {
            HeaderResponse headerResponse = downloader.fetchHeaders(uri, headers);
            long contentLength = headerResponse.getContentLength();

            createTargetFile(task.getTarget());
            RandomAccessFile targetFile = new RandomAccessFile(task.getTarget(), "rw");
            targetFile.setLength(contentLength);
            closeQuietly(targetFile);

            long perThreadLength = DEFAULT_PER_THREAD_LENGTH;
            long threadCount;
            if (contentLength <= perThreadLength) {
                threadCount = 2;
                perThreadLength = contentLength / threadCount;
            } else {
                threadCount = contentLength / perThreadLength;
            }

            long remainder = contentLength % threadCount;
            List<SplitTask> splitTasks = new ArrayList<>();
            System.out.println("threadCount = " + threadCount);
            for (int count = 0; count < threadCount; count++) {
                long startPosition = count * perThreadLength;
                long endPosition = startPosition + perThreadLength - 1;
                if (count == threadCount - 1) {
                    endPosition = startPosition + perThreadLength + remainder - 1;
                }
                splitTasks.add(buildSplitTask(startPosition, endPosition));
            }
            dispatcherEvent(new TaskSplitEvent(new CompositeEventTaskWrapper(task, splitTasks, contentLength)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SplitTask buildSplitTask(long startPosition, long endPosition) {
        return new SplitHunterTask(task, startPosition, endPosition);
    }

}
