package com.bestxty.sault.hunter;

import com.bestxty.sault.event.EventDispatcher;
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


    public ProgressSupportedHunter(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
    }


    protected void copySteamAndAutoClose(InputStream stream, RandomAccessFile output,
                                         long startPosition, long totalSize) throws IOException {
        try {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            long finishLength = 0;
            output.seek(startPosition);
            while (EOF != (length = stream.read(buffer))) {
                output.write(buffer, 0, length);
                finishLength += length;
                dispatcherEvent(new HunterProgressEvent(this, calculateProgress(finishLength, totalSize)));
            }
        } finally {
            closeQuietly(output);
            closeQuietly(stream);
        }
    }

}
