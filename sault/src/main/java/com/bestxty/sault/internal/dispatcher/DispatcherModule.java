package com.bestxty.sault.internal.dispatcher;

import android.os.Looper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */
@Module
public final class DispatcherModule {


    @Provides
    @Singleton
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
}
