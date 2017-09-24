package com.bestxty.sault;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */
public class SaultConfigurationTest extends ApplicationTestCase {
    @Test
    public void getKey() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertNotNull(configuration.getKey());
    }

    @Test
    public void getSaveDir() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertNull(configuration.getSaveDir());

        configuration = new SaultConfiguration.Builder()
                .saveDir("testdir")
                .build();
        assertNotNull(configuration.getSaveDir());
    }

    @Test
    public void getService() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertTrue(configuration.getService() instanceof SaultExecutorService);
        ExecutorService executorService = Mockito.mock(ExecutorService.class);
        configuration = new SaultConfiguration.Builder().executor(executorService).build();
        assertEquals(executorService, configuration.getService());
    }

    @Test
    public void getDownloader() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertTrue(configuration.getDownloader() instanceof OkHttpDownloader);
        Downloader downloader = Mockito.mock(Downloader.class);
        configuration = new SaultConfiguration.Builder().downloader(downloader).build();
        assertEquals(downloader, configuration.getDownloader());
    }

    @Test
    public void isLoggingEnabled() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertFalse(configuration.isLoggingEnabled());
        configuration = new SaultConfiguration.Builder().loggingEnabled(true).build();
        assertTrue(configuration.isLoggingEnabled());
    }

    @Test
    public void isBreakPointEnabled() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertTrue(configuration.isBreakPointEnabled());
        configuration = new SaultConfiguration.Builder().breakPointEnabled(false).build();
        assertFalse(configuration.isBreakPointEnabled());
    }

    @Test
    public void isMultiThreadEnabled() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertTrue(configuration.isMultiThreadEnabled());
        configuration = new SaultConfiguration.Builder().multiThreadEnabled(false).build();
        assertFalse(configuration.isMultiThreadEnabled());
    }

    @Test
    public void isAutoAdjustThreadEnabled() throws Exception {
        SaultConfiguration configuration = new SaultConfiguration.Builder()
                .build();
        assertTrue(configuration.isAutoAdjustThreadEnabled());
        configuration = new SaultConfiguration.Builder().autoAdjustThreadEnabled(false).build();
        assertFalse(configuration.isAutoAdjustThreadEnabled());
    }

}