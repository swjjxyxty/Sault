package com.bestxty.dl;

import com.bestxty.dl.Downloader.ContentLengthException;
import com.bestxty.dl.Downloader.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import static com.bestxty.dl.Utils.DEFAULT_BUFFER_SIZE;
import static com.bestxty.dl.Utils.EOF;
import static com.bestxty.dl.Utils.THREAD_IDLE_NAME;
import static com.bestxty.dl.Utils.closeQuietly;
import static com.bestxty.dl.Utils.createTargetFile;
import static com.bestxty.dl.Utils.log;
import static java.lang.Thread.currentThread;

/**
 * @author xty
 *         Created by xty on 2017/2/18.
 */
abstract class AbstractSaultTaskHunter extends BaseSaultTaskHunter {


    AbstractSaultTaskHunter(Sault sault, Dispatcher dispatcher, Task task, Downloader downloader) {
        super(sault, dispatcher, task, downloader);
    }

    abstract long getStartPosition();

    abstract long getEndPosition();

    abstract void onProgress(int length);

    abstract void onStart(long totalSize);

    abstract void onFinish();

    abstract void onError(Exception exception);

    private void hunter() throws IOException {

        final long startPosition = getStartPosition();

        final long endPosition = getEndPosition();

        if (endPosition > 0L
                && endPosition < startPosition) {
            throw new IllegalArgumentException("end position must greater than start position.");
        }

        Response response = downloader.load(task.getUri(), startPosition, endPosition);

        InputStream stream = response.stream;
        if (stream == null) {
            log("stream is null");
            return;
        }

        if (response.contentLength == 0) {
            closeQuietly(stream);
            throw new ContentLengthException("Received response with 0 content-length header.");
        }

        onStart(response.contentLength);

        createTargetFile(task.getTarget());

        RandomAccessFile output = new RandomAccessFile(task.getTarget(), "rw");

        output.seek(startPosition);

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while (EOF != (length = stream.read(buffer))) {
                output.write(buffer, 0, length);
                onProgress(length);
            }

        } finally {
            closeQuietly(output);
            closeQuietly(stream);
        }

    }


    @Override
    public void run() {
        updateThreadName();
        try {
            hunter();
            onFinish();
        } catch (Exception e) {
            setException(e);
            onError(e);
        } finally {
            currentThread().setName(THREAD_IDLE_NAME);
        }
    }

}
