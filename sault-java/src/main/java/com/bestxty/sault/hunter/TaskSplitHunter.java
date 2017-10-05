package com.bestxty.sault.hunter;

import com.bestxty.sault.SplitHunterTask;
import com.bestxty.sault.SplitTask;
import com.bestxty.sault.Task;
import com.bestxty.sault.TaskWrapper;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.downloader.HeaderResponse;
import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.EventDispatcher;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
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
public class TaskSplitHunter extends SimpleCancelableHunter {

    private static final long DEFAULT_PER_THREAD_LENGTH = 1024 * 1024 * 10;//10M

    private Downloader downloader;
    private EventDispatcher eventDispatcher;
    private Task task;

    public TaskSplitHunter(Downloader downloader, EventDispatcher eventDispatcher, Task task) {
        this.downloader = downloader;
        this.eventDispatcher = eventDispatcher;
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
            for (int count = 0; count < threadCount; count++) {
                long startPosition = count * perThreadLength;
                long endPosition = startPosition + perThreadLength - 1;
                if (count == threadCount - 1) {
                    endPosition = startPosition + perThreadLength + remainder - 1;
                }
                splitTasks.add(buildSplitTask(startPosition, endPosition));
            }
            eventDispatcher.dispatcherEvent(new TaskSplitEvent(new TaskWrapper(task, splitTasks)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SplitTask buildSplitTask(long startPosition, long endPosition) {
        return new SplitHunterTask(task, startPosition, endPosition);
    }

}
