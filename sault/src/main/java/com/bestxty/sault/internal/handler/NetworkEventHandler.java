package com.bestxty.sault.internal.handler;

import android.net.NetworkInfo;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/24.
 */

public interface NetworkEventHandler {

    int AIRPLANE_MODE_CHANGE = 400;

    int NETWORK_CHANGE = 401;

    void handleAirplaneModeChange(boolean airplaneMode);

    void handleNetworkChange(NetworkInfo networkInfo);
}
