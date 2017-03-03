package com.bestxty.sault;

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
     * @param uri           uri
     * @param startPosition resource start position
     * @return response {@link Response}
     * @throws IOException exception
     */
    Response load(Uri uri, long startPosition) throws IOException;

    /**
     * load resource from uri.
     *
     * @param uri           uri
     * @param startPosition resource start position
     * @param endPosition   resource end position
     * @return response {@link Response}
     * @throws IOException exception
     */
    Response load(Uri uri, long startPosition, long endPosition) throws IOException;

    /**
     * detect uri resource content length
     *
     * @param uri uri
     * @throws IOException exception
     */
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
