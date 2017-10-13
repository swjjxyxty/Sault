package com.bestxty.sault.hunter;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Downloader.ContentLengthException;
import com.bestxty.sault.Downloader.Response;
import com.bestxty.sault.Downloader.ResponseException;
import com.bestxty.sault.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.task.PartedSaultTask;
import com.bestxty.sault.task.SaultTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;

import static com.bestxty.sault.Utils.DEFAULT_BUFFER_SIZE;
import static com.bestxty.sault.Utils.EOF;
import static com.bestxty.sault.Utils.closeQuietly;
import static com.bestxty.sault.Utils.log;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/12.
 */

public class DefaultSaultTaskHunter extends AbstractTaskHunter {

    private final HunterEventDispatcher eventDispatcher;
    private Exception exception;

    public DefaultSaultTaskHunter(PartedSaultTask task, Downloader downloader,
                                  HunterEventDispatcher eventDispatcher) {
        super(task, downloader);
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    void hunter() {
        try {
            PartedSaultTask task = (PartedSaultTask) getTask();
            long startPosition = task.getStartPosition();
            long endPosition = task.getEndPosition();
            Downloader downloader = getDownloader();
            Response response =
                    downloader.load(task.getUri(), startPosition, endPosition);
            InputStream stream = response.stream;
            if (stream == null) {
                log("stream is null");
                throw new ContentLengthException("Stream is null.");
            }

            if (response.contentLength == 0) {
                closeQuietly(stream);
                throw new ContentLengthException("Received response with 0 content-length header.");
            }

            copyStream(stream, startPosition);

            eventDispatcher.dispatchHunterFinish(this);

        } catch (InterruptedIOException | ResponseException | ContentLengthException e) {
            exception = e;
            eventDispatcher.dispatchHunterFailed(this);
        } catch (IOException e) {
            exception = e;
            eventDispatcher.dispatchHunterRetry(this);
        } catch (Exception e) {
            exception = e;
            eventDispatcher.dispatchHunterException(this);
        }
    }

    private void copyStream(InputStream stream, long startPosition) throws Exception {
        SaultTask task = getTask();
        RandomAccessFile output = new RandomAccessFile(task.getTarget(), "rw");
        output.seek(startPosition);

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while (EOF != (length = stream.read(buffer))) {
                output.write(buffer, 0, length);
                task.notifyFinishedSize(length);
            }

        } finally {
            closeQuietly(output);
            closeQuietly(stream);
        }

    }
}
