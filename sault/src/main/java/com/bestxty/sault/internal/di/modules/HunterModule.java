package com.bestxty.sault.internal.di.modules;

import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.di.PerTask;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@Module
public class HunterModule {

    @Provides
    @Named("hunterSequence")
    @PerTask
    Integer provideHunterSequence() {
        return Utils.generateHunterSequence();
    }
}
