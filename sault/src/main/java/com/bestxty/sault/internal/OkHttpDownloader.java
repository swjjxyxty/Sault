package com.bestxty.sault.internal;

import android.net.NetworkInfo;
import android.net.Uri;

import com.bestxty.sault.Downloader;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author xty
 *         Created by xty on 2016/12/7.
 */
public class OkHttpDownloader implements Downloader {

    private static OkHttpClient defaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(Utils.DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(Utils.DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();
    }

    private final OkHttpClient client;

    public OkHttpDownloader() {
        this(defaultOkHttpClient());
    }

    public OkHttpDownloader(OkHttpClient client) {
        this.client = client;
    }


    private okhttp3.Response internalCall(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    private Response handleResponse(okhttp3.Response response) throws ResponseException {
        ResponseBody body = response.body();
        if (!response.isSuccessful()) {
            if (body != null) body.close();
            throw new ResponseException(response.code() + " " + response.message(), response.code());
        }

        return new Response(body == null ? null : body.byteStream(), body == null ? 0 : body.contentLength());
    }


    @Override
    public Response load(Uri uri, long startPosition) throws IOException {
        okhttp3.Response response = internalCall(new Request.Builder()
                .header("Range", "bytes=" + startPosition + "-")
                .url(uri.toString())
                .build());

        return handleResponse(response);
    }

    @Override
    public Response load(Uri uri, long startPosition, long endPosition) throws IOException {
        if (endPosition > 0L
                && endPosition < startPosition) {
            throw new IllegalArgumentException("end position must greater than start position.");
        }

        if (endPosition == 0L && startPosition >= 0L) {
            return load(uri, startPosition);
        }

        okhttp3.Response response = internalCall(new Request.Builder()
                .header("Range", "bytes=" + startPosition + "-" + endPosition)
                .url(uri.toString())
                .build());

        return handleResponse(response);
    }


    @Override
    public long fetchContentLength(Uri uri) throws IOException {

        okhttp3.Response response = internalCall(new Request.Builder()
                .url(uri.toString())
                .head()
                .build());

        String str = response.header("Content-Length");
        if (str == null || "".equals(str)) {
            return -1L;
        }

        return Long.parseLong(str);

    }

    @Override
    public boolean supportBreakPoint() {
        return true;
    }

    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return !airplaneMode && (info == null || info.isConnected());
    }

    @Override
    public int getRetryCount() {
        return 2;
    }
}
