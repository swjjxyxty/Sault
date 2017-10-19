package com.bestxty.sault.internal.di.components;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.internal.dispatcher.DispatcherModule;
import com.bestxty.sault.internal.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.internal.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.internal.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.internal.handler.HandlerModule;
import com.bestxty.sault.internal.task.PartedSaultTask;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
@Component(modules = {SaultModule.class, HandlerModule.class, DispatcherModule.class})
public interface SaultComponent {

    void inject(Sault sault);

    void inject(PartedSaultTask saultTask);

    Downloader downloader();

    @Named("retryCount")
    AtomicInteger retryCount();

    HunterEventDispatcher hunterEventDispatcher();

    TaskRequestEventDispatcher taskRequestEventDispatcher();

}
