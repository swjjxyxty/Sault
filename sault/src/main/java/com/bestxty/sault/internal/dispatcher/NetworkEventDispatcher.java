package com.bestxty.sault.internal.dispatcher;

import android.net.NetworkInfo;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/24.
 */

public interface NetworkEventDispatcher {

    void dispatchAirplaneModeChange(boolean airplaneMode);

    void dispatchNetworkChange(NetworkInfo networkInfo);
}
