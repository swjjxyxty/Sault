package com.bestxty.dl;

import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
public interface Downloader {
    Response load(Uri uri) throws IOException;

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info);

    boolean supportsReplay();

    int getRetryCount();

    class ContentLengthException extends IOException {
        ContentLengthException(String message) {
            super(message);
        }
    }

    class ResponseException extends IOException {
        final int responseCode;

        ResponseException(String message, int responseCode) {
            super(message);
            this.responseCode = responseCode;
        }
    }

    class Response {
        InputStream stream;
        long contentLength;

        Response(InputStream stream, long contentLength) {
            this.stream = stream;
            this.contentLength = contentLength;
        }
    }

}
