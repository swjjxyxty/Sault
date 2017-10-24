package com.bestxty.sault.internal.handler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */

@Module
public final class HandlerModule {

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


    @Singleton
    @Provides
    NetworkEventHandler provideNetworkEventHandler(DefaultEventHandler defaultEventHandler) {
        return defaultEventHandler;
    }

}
