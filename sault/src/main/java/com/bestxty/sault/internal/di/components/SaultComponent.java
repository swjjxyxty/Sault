package com.bestxty.sault.internal.di.components;

import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.task.SaultTask;

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

    void inject(SaultTask saultTask);
}
