package com.bestxty.dl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.bestxty.dl.Sault;
import com.bestxty.dl.aidl.IRemoteDownloadInterface;

import java.io.File;

/**
 * @author xty
 *         Created by xty on 2016/12/10.
 */
public class RemoteDownloadService extends Service implements ServiceBridge {

    private Sault sault;

    private DownloadBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        sault = new Sault.Builder(this)
                .saveDir(Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "dl_test")
                .build();
        binder = new DownloadBinder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sault != null) {
            sault.shutdown();
        }
        binder = null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}