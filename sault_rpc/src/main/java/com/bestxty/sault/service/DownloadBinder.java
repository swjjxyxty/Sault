package com.bestxty.sault.service;

import android.os.RemoteException;

import com.bestxty.sault.aidl.IRemoteDownloadInterface;

/**
 * @author xty
 *         Created by xty on 2016/12/10.
 */
class DownloadBinder extends IRemoteDownloadInterface.Stub {

    private final ServiceBridge bridge;

    DownloadBinder(ServiceBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public String start(String url) throws RemoteException {
        return null;
    }

    @Override
    public String pause(String key) throws RemoteException {
        return null;
    }

    @Override
    public String resume(String key) throws RemoteException {
        return null;
    }

    @Override
    public String cancel(String key) throws RemoteException {
        return null;
    }
}
