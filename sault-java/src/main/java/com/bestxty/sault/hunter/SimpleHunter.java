package com.bestxty.sault.hunter;

import com.bestxty.sault.SplitTask;
import com.bestxty.sault.Task;
import com.bestxty.sault.downloader.ContentLengthException;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.downloader.Response;
import com.bestxty.sault.downloader.ResponseException;
import com.bestxty.sault.event.EventDispatcher;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;
import com.bestxty.sault.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Map;

import static com.bestxty.sault.utils.IOUtils.closeQuietly;
import static com.bestxty.sault.utils.Utils.THREAD_IDLE_NAME;
import static com.bestxty.sault.utils.Utils.log;
import static java.lang.Thread.currentThread;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class SimpleHunter extends ProgressSupportedHunter {

    private Downloader downloader;
    private Task task;

    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(Utils.THREAD_PREFIX);
        }
    };

    public SimpleHunter(Downloader downloader, EventDispatcher eventDispatcher, Task task) {
        super(eventDispatcher);
        this.downloader = downloader;
        this.task = task;
    }

    @Override
    public void run() {
        updateThreadName();
        try {
            dispatcherEvent(new HunterStartEvent(this));
            Map<String, String> headers = task.getHeaderMap() != null
                    ? task.getHeaderMap() : Collections.<String, String>emptyMap();
            long startPosition = -1L;
            long endPosition = -1L;
            if (task instanceof SplitTask) {
                startPosition = ((SplitTask) task).getStartPosition();
                endPosition = ((SplitTask) task).getEndPosition();
            }

            Response response = downloader.load(task.getUri(), headers, startPosition, endPosition);

            InputStream stream = response.getStream();
            if (stream == null) {
                log("stream is null");
                return;
            }

            if (response.getContentLength() == 0) {
                closeQuietly(stream);
                throw new ContentLengthException("Received response with 0 content-length header.");
            }

            RandomAccessFile output = new RandomAccessFile(task.getTarget(), "rw");

            copySteamAndAutoClose(stream, output, startPosition, response.getContentLength());

            dispatcherEvent(new HunterCompleteEvent(this));

        } catch (ResponseException ex) {
        } catch (ContentLengthException ex) {
        } catch (InterruptedIOException ex) {
        } catch (IOException ex) {
        } catch (Exception ex) {

        } finally {
            Thread.currentThread().setName(THREAD_IDLE_NAME);
        }
    }


    private void updateThreadName() {
        String name = task.getTaskId();
        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
        builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);
        currentThread().setName(builder.toString());
    }

}
