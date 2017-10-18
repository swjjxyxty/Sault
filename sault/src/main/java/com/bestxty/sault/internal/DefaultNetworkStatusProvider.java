package com.bestxty.sault.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bestxty.sault.NetworkStatusProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bestxty.sault.internal.Utils.getService;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */
@Singleton
public class DefaultNetworkStatusProvider extends BroadcastReceiver implements NetworkStatusProvider {

    private final Context context;
    private final boolean accessNetwork;
    private final List<NetworkStatusListener> listeners;

    @Inject
    public DefaultNetworkStatusProvider(Context context) {
        this.context = context;
        this.accessNetwork = Utils.hasPermission(context, ACCESS_NETWORK_STATE);
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addNetworkStatusListener(NetworkStatusListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeAllNetworkStatusListeners() {
        listeners.clear();
    }

    @Override
    public void removeNetworkStatusListener(NetworkStatusListener listener) {
        if (listener != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public boolean accessNetwork() {
        return accessNetwork;
    }

    @Override
    public boolean isAirplaneMode() {
        return Utils.isAirplaneModeOn(context);
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }


    static final String EXTRA_AIRPLANE_STATE = "state";

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
        if (accessNetwork) {
            filter.addAction(CONNECTIVITY_ACTION);
        }
        context.registerReceiver(this, filter);
    }


    public void unregister() {
        context.unregisterReceiver(this);
    }


    private void dispatchAirplaneModeChange(boolean airplaneMode) {
        for (NetworkStatusListener listener : listeners) {
            listener.airplaneModeChange(airplaneMode);
        }
    }

    private void dispatchNetworkStateChange(NetworkInfo networkInfo) {
        for (NetworkStatusListener listener : listeners) {
            listener.networkChange(networkInfo);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // On some versions of Android this may be called with a null Intent,
        // also without extras (getExtras() == null), in such case we use defaults.
        if (intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
                return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
            }
            dispatchAirplaneModeChange(intent.getBooleanExtra(EXTRA_AIRPLANE_STATE, false));
        } else if (CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
            dispatchNetworkStateChange(connectivityManager.getActiveNetworkInfo());
        }
    }

}
