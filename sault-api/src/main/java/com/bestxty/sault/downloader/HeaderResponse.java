package com.bestxty.sault.downloader;

import java.util.List;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class HeaderResponse {
    private Map<String, List<String>> headerMap;
    private long contentLength;

    public HeaderResponse(Map<String, List<String>> headerMap, long contentLength) {
        this.headerMap = headerMap;
        this.contentLength = contentLength;
    }

    public Map<String, List<String>> getHeaderMap() {
        return headerMap;
    }

    public long getContentLength() {
        return contentLength;
    }
}
