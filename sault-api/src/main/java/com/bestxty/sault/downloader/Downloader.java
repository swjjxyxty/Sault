package com.bestxty.sault.downloader;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public interface Downloader {

    Response load(URI uri, Map<String, String> headers, long startPosition, long endPosition) throws IOException;

    HeaderResponse fetchHeaders(URI uri, Map<String, String> headers) throws IOException;

    boolean shouldRetry(Network network);

    boolean supportBreakPoint();

    int getRetryCount();

}
