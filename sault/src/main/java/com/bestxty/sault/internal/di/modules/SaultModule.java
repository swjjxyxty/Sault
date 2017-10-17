package com.bestxty.sault.internal.di.modules;

import android.content.Context;
import android.os.Looper;

import com.bestxty.sault.DefaultNetworkStatusProvider;
import com.bestxty.sault.Downloader;
import com.bestxty.sault.NetworkStatusProvider;
import com.bestxty.sault.SaultConfiguration;
import com.bestxty.sault.dispatcher.DefaultHunterEventDispatcher;
import com.bestxty.sault.dispatcher.DefaultSaultTaskEventDispatcher;
import com.bestxty.sault.dispatcher.DispatcherThread;
import com.bestxty.sault.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.handler.DefaultEventHandler;
import com.bestxty.sault.handler.DefaultSaultTaskEventHandler;
import com.bestxty.sault.handler.HunterEventHandler;
import com.bestxty.sault.handler.SaultTaskEventHandler;
import com.bestxty.sault.handler.TaskRequestEventHandler;

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
public class SaultModule {

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
    ExecutorService provideExecutorService() {
        return configuration.getService();
    }

    @Provides
    Downloader provideDownloader() {
        return configuration.getDownloader();
    }

    @Provides
    @Named("retryCount")
    AtomicInteger provideRetryCount() {
        return new AtomicInteger(configuration.getDownloader().getRetryCount());
    }

    @Provides
    File provideSaveDir() {
        return configuration.getSaveDir();
    }

    @Provides
    @Named("saultKey")
    String provideSaultKey() {
        return configuration.getKey();
    }

    @Provides
    @Named("loggingEnabled")
    Boolean provideLoggingEnabled() {
        return configuration.isLoggingEnabled();
    }

    @Provides
    @Named("breakPointEnabled")
    Boolean provideBreakPointEnabled() {
        return configuration.isBreakPointEnabled();
    }

    @Provides
    @Named("multiThreadEnabled")
    Boolean provideMultiThreadEnabled() {
        return configuration.isMultiThreadEnabled();
    }

    @Singleton
    @Provides
    NetworkStatusProvider provide(DefaultNetworkStatusProvider networkStatusProvider) {
        return networkStatusProvider;
    }


    @Provides
    @Named("mainLooper")
    Looper provideMainLooper() {
        return Looper.getMainLooper();
    }

    @Provides
    @Named("internalLooper")
    Looper provideInternalLooper(DispatcherThread dispatcherThread) {
        dispatcherThread.start();
        return dispatcherThread.getLooper();
    }


    @Singleton
    @Provides
    HunterEventDispatcher provideHunterEventDispatcher(DefaultHunterEventDispatcher defaultHunterEventDispatcher) {
        return defaultHunterEventDispatcher;
    }

    @Singleton
    @Provides
    SaultTaskEventDispatcher provideSaultTaskEventDispatcher(DefaultSaultTaskEventDispatcher defaultSaultTaskEventDispatcher) {
        return defaultSaultTaskEventDispatcher;
    }

    @Singleton
    @Provides
    TaskRequestEventDispatcher provideTaskRequestEventDispatcher(DefaultHunterEventDispatcher defaultHunterEventDispatcher) {
        return defaultHunterEventDispatcher;
    }

    //
    @Singleton
    @Provides
    HunterEventHandler provideHunterEventHandler(DefaultEventHandler defaultEventHandler) {
        return defaultEventHandler;
    }

    @Singleton
    @Provides
    SaultTaskEventHandler provideSaultTaskEventHandler(DefaultSaultTaskEventHandler saultTaskEventHandler) {
        return saultTaskEventHandler;
    }

    @Singleton
    @Provides
    TaskRequestEventHandler provideTaskRequestEventHandler(DefaultEventHandler defaultEventHandler) {
        return defaultEventHandler;
    }

}
