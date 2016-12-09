package com.bestxty.dl;

import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author xty
 *         Created by xty on 2016/12/7.
 */
class OkHttpDownloader implements Downloader {

    private static OkHttpClient defaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(Utils.DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(Utils.DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();
    }

    private final OkHttpClient client;

    OkHttpDownloader() {
        this(defaultOkHttpClient());
    }

    OkHttpDownloader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response load(Uri uri) throws IOException {
        okhttp3.Response response = client.newCall(new Request.Builder()
                .url(uri.toString())
                .build())
                .execute();

        if (!response.isSuccessful()) {
            response.body().close();
            throw new ResponseException(response.code() + " " + response.message(), response.code());
        }

        ResponseBody body = response.body();

        return new Response(body.byteStream(), body.contentLength());
    }


    @Override
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return info == null || info.isConnected();
    }

    @Override
    public boolean supportsReplay() {
        return true;
    }

    @Override
    public int getRetryCount() {
        return 2;
    }
}
