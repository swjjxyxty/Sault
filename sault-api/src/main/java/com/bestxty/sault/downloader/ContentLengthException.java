package com.bestxty.sault.downloader;

import java.io.IOException;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class ContentLengthException extends IOException {
    public ContentLengthException() {
    }

    public ContentLengthException(String message) {
        super(message);
    }

    public ContentLengthException(String message, Throwable cause) {
        super(message, cause);
    }
}