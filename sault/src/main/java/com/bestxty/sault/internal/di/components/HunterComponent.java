package com.bestxty.sault.internal.di.components;

import com.bestxty.sault.hunter.DefaultSaultTaskHunter;
import com.bestxty.sault.hunter.PartingSaultTaskHunter;
import com.bestxty.sault.internal.di.PerTask;
import com.bestxty.sault.internal.di.modules.HunterModule;

import dagger.Component;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PerTask
@Component(dependencies = SaultComponent.class, modules = {HunterModule.class})
public interface HunterComponent {

    void inject(DefaultSaultTaskHunter taskHunter);

    void inject(PartingSaultTaskHunter taskHunter);
}
