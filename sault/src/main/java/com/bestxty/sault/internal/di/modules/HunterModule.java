package com.bestxty.sault.internal.di.modules;

import com.bestxty.sault.Utils;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Module(includes = SaultModule.class)
public class HunterModule {

//    private final SaultTask task;
//
//    public HunterModule(SaultTask task) {
//        this.task = task;
//    }

    @Provides
    @Named("hunterSequence")
    Integer provideHunterSequence() {
        return Utils.generateHunterSequence();
    }
}
