package com.bestxty.sault.downloader;

import com.bestxty.sault.utils.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class HttpURLConnectionDownloader implements Downloader {


    @Override
    public Response load(URI uri, Map<String, String> headers, long startPosition, long endPosition) throws IOException {
        startPosition = fixStartPosition(startPosition);
        endPosition = fixEndPosition(startPosition, endPosition);

        URL url = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setHeaders(connection, headers);
        setAcceptRangeHeader(connection, startPosition, endPosition);
        int code = connection.getResponseCode();
        if (!isSuccessful(code)) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(connection.getErrorStream(), writer, StandardCharsets.UTF_8);
            String message = writer.getBuffer().toString();
            IOUtils.close(connection);
            throw new ResponseException(code + " " + message, code);
        }

        return new Response(connection.getInputStream(),
                connection.getHeaderFields(), connection.getContentLengthLong());

    }

    @Override
    public HeaderResponse fetchHeaders(URI uri, Map<String, String> headers) throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        setHeaders(connection, headers);
        int code = connection.getResponseCode();
        if (!isSuccessful(code)) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(connection.getErrorStream(), writer, StandardCharsets.UTF_8);
            String message = writer.getBuffer().toString();
            IOUtils.close(connection);
            throw new ResponseException(code + " " + message, code);
        }

        return new HeaderResponse(connection.getHeaderFields(), connection.getContentLengthLong());
    }

    @Override
    public boolean shouldRetry(Network network) {
        return network == Network.CONNECTED;
    }

    @Override
    public boolean supportBreakPoint() {
        return true;
    }

    @Override
    public int getRetryCount() {
        return 2;
    }

    private boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    private void setHeaders(HttpURLConnection connection, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    private void setAcceptRangeHeader(HttpURLConnection connection, long startPosition, long endPosition) {
        if (endPosition == -1L) {
            connection.setRequestProperty("Range", "bytes=" + startPosition + "-");
        }
        connection.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
    }

    private long fixStartPosition(long startPosition) {
        return startPosition < 0 ? 0 : startPosition;
    }

    private long fixEndPosition(long startPosition, long endPosition) {
        return endPosition < startPosition ? -1L : endPosition;
    }
}
