package com.bestxty.sault.hunter;

import com.bestxty.sault.Task;
import com.bestxty.sault.downloader.Downloader;
import com.bestxty.sault.event.EventCallbackExecutor;
import com.bestxty.sault.event.hunter.HunterProgressEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import static com.bestxty.sault.utils.IOUtils.DEFAULT_BUFFER_SIZE;
import static com.bestxty.sault.utils.IOUtils.EOF;
import static com.bestxty.sault.utils.IOUtils.closeQuietly;
import static com.bestxty.sault.utils.Utils.calculateProgress;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public abstract class ProgressSupportedHunter extends EventSupportedHunter {

    public ProgressSupportedHunter(Downloader downloader, Task task, EventCallbackExecutor eventCallbackExecutor) {
        super(downloader, task, eventCallbackExecutor);
    }

    protected void copySteamAndAutoClose(InputStream stream, RandomAccessFile output,
                                         long startPosition) throws IOException {
        try {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            output.seek(startPosition);
            while (EOF != (length = stream.read(buffer))) {
                output.write(buffer, 0, length);
                dispatcherEvent(new HunterProgressEvent(this, length));
            }
        } finally {
            closeQuietly(output);
            closeQuietly(stream);
        }
    }

}
