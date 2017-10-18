package com.bestxty.sault;

import android.net.NetworkInfo;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */

public interface NetworkStatusProvider {

    boolean accessNetwork();

    boolean isAirplaneMode();

    NetworkInfo getNetworkInfo();

    void addNetworkStatusListener(NetworkStatusListener listener);

    void removeNetworkStatusListener(NetworkStatusListener listener);

    void removeAllNetworkStatusListeners();

    interface NetworkStatusListener {
        void airplaneModeChange(boolean airplaneMode);

        void networkChange(NetworkInfo networkInfo);
    }
}
