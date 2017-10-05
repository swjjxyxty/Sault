package com.bestxty.sault.downloader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class Response extends HeaderResponse implements Closeable {

    private InputStream stream;

    public Response(InputStream stream, Map<String, List<String>> headerMap, long contentLength) {
        super(headerMap, contentLength);
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }
}
