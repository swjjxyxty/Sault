package com.bestxty.sault.demo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.bestxty.sault.Sault;
import com.bestxty.sault.SaultConfiguration;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author xty
 *         Created by xty on 2016/11/29.
 */
public class DownloadApplication extends Application {

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        mRefWatcher = LeakCanary.install(this);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);


        SaultConfiguration saultConfiguration = new SaultConfiguration.Builder()
                .saveDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dl_test")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build())
                .loggingEnabled(true)
                .build();

        Sault.setDefaultConfiguration(saultConfiguration);


    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((DownloadApplication) context.getApplicationContext()).mRefWatcher;
    }
}
