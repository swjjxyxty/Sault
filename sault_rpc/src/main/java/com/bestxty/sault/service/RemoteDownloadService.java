package com.bestxty.sault.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bestxty.sault.Sault;

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
//        sault = new Sault.SaultConfiguration(this)
//                .saveDir(Environment
//                        .getExternalStorageDirectory()
//                        .getAbsolutePath() + File.separator + "dl_test")
//                .build();
        binder = new DownloadBinder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sault != null) {
            sault.close();
        }
        binder = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
