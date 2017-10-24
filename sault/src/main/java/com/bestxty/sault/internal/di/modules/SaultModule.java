package com.bestxty.sault.internal.di.modules;

import android.content.Context;
import android.os.Looper;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.SaultConfiguration;
import com.bestxty.sault.internal.DefaultNetworkStatusProvider;
import com.bestxty.sault.internal.NetworkStatusProvider;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Module
public final class SaultModule {

    private final Context context;
    private final SaultConfiguration configuration;

    public SaultModule(Context context, SaultConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    @Singleton
    @Provides
    Context provideApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    ExecutorService provideExecutorService() {
        return configuration.getService();
    }

    @Provides
    @Singleton
    Downloader provideDownloader() {
        return configuration.getDownloader();
    }

    @Provides
    @Named("retryCount")
    AtomicInteger provideRetryCount() {
        return new AtomicInteger(configuration.getDownloader().getRetryCount());
    }

    @Provides
    @Singleton
    File provideSaveDir() {
        return configuration.getSaveDir();
    }

    @Provides
    @Named("saultKey")
    @Singleton
    String provideSaultKey() {
        return configuration.getKey();
    }

    @Provides
    @Named("loggingEnabled")
    @Singleton
    Boolean provideLoggingEnabled() {
        return configuration.isLoggingEnabled();
    }

    @Provides
    @Named("breakPointEnabled")
    @Singleton
    Boolean provideBreakPointEnabled() {
        return configuration.isBreakPointEnabled();
    }

    @Provides
    @Named("multiThreadEnabled")
    @Singleton
    Boolean provideMultiThreadEnabled() {
        return configuration.isMultiThreadEnabled();
    }

    @Singleton
    @Provides
    NetworkStatusProvider provide(DefaultNetworkStatusProvider networkStatusProvider) {
        return networkStatusProvider;
    }

    @Provides
    @Singleton
    @Named("mainLooper")
    Looper provideMainLooper() {
        return Looper.getMainLooper();
    }


}
