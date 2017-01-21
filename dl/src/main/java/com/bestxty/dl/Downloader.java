package com.bestxty.dl;

import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
@SuppressWarnings("WeakerAccess")
public interface Downloader {

    /**
     * load resource from uri.
     *
     * @param uri uri
     * @return response {@link Response}
     * @throws IOException
     */
    Response load(Uri uri) throws IOException;

    Response load(Uri uri, long startPosition) throws IOException;

    Response load(Uri uri, long startPosition, long endPosition) throws IOException;

    long fetchContentLength(Uri uri) throws IOException;

    /**
     * determine whether task can try again.
     *
     * @param airplaneMode current network mode.
     * @param info         network info {@link NetworkInfo}
     * @return if can retry to return true.otherwise return false.
     */
    boolean shouldRetry(boolean airplaneMode, NetworkInfo info);

    /**
     * judge downloader supports replay.
     *
     * @return if supports replay return true.
     */
    boolean supportsReplay();

    /**
     * judge downloader supports break point.
     *
     * @return if supports return true.
     */
    boolean supportBreakPoint();

    /**
     * get retry count.
     *
     * @return return download retry count.
     */
    int getRetryCount();

    /**
     * content length exception.
     */
    class ContentLengthException extends IOException {
        ContentLengthException(String message) {
            super(message);
        }
    }

    /**
     * response exception
     */
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
