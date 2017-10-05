package com.bestxty.sault.downloader;

import java.io.IOException;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class ResponseException extends IOException {
    private final int responseCode;

    public ResponseException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
}
