package com.bestxty.dl.demo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

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

    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((DownloadApplication) context.getApplicationContext()).mRefWatcher;
    }
}
