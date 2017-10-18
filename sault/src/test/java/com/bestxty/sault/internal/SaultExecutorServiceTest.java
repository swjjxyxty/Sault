package com.bestxty.sault.internal;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.internal.SaultExecutorService;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */
public class SaultExecutorServiceTest extends ApplicationTestCase {

    @Test
    public void adjustThreadCount() throws Exception {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        final int defaultThreadCount = 3;
        SaultExecutorService executorService = new SaultExecutorService();
        assertEquals(defaultThreadCount, executorService.getCorePoolSize());
        assertEquals(defaultThreadCount, executorService.getMaximumPoolSize());

        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        executorService.adjustThreadCount(networkInfo);
        final int wifiThreadCount = 4;
        assertEquals(wifiThreadCount, executorService.getCorePoolSize());
        assertEquals(wifiThreadCount, executorService.getMaximumPoolSize());

        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_LTE);
        executorService.adjustThreadCount(networkInfo);
        final int lteThreadCount = 3;
        assertEquals(lteThreadCount, executorService.getCorePoolSize());
        assertEquals(lteThreadCount, executorService.getMaximumPoolSize());

        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_CDMA);
        executorService.adjustThreadCount(networkInfo);
        final int cdmaThreadCount = 2;
        assertEquals(cdmaThreadCount, executorService.getCorePoolSize());
        assertEquals(cdmaThreadCount, executorService.getMaximumPoolSize());

        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_GPRS);
        executorService.adjustThreadCount(networkInfo);
        final int gprsThreadCount = 1;
        assertEquals(gprsThreadCount, executorService.getCorePoolSize());
        assertEquals(gprsThreadCount, executorService.getMaximumPoolSize());

        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        executorService.adjustThreadCount(networkInfo);
        assertEquals(defaultThreadCount, executorService.getCorePoolSize());
        assertEquals(defaultThreadCount, executorService.getMaximumPoolSize());
    }

    @Test
    public void submit() throws Exception {
        SaultExecutorService executorService = new SaultExecutorService();
        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //nothing
                }
            });
        } catch (Exception e) {
            assertEquals("task class must implements TaskHunter!", e.getMessage());
        }
    }

}