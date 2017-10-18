package com.bestxty.sault.internal.di.components;

import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.task.PartedSaultTask;
import com.bestxty.sault.task.SaultTask;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Singleton
@Component(modules = {SaultModule.class})
public interface SaultComponent {

    void inject(Sault sault);

    void inject(PartedSaultTask saultTask);

    Downloader downloader();

    @Named("retryCount")
    AtomicInteger retryCount();

    HunterEventDispatcher hunterEventDispatcher();

    TaskRequestEventDispatcher taskRequestEventDispatcher();

    SaultTaskEventDispatcher saultTaskEventDispatcher();


}
