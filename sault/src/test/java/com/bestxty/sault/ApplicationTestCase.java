package com.bestxty.sault;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, shadows = {ShadowLog.class, ShadowLooper.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*", "sun.security.*", "javax.net.*"})
public abstract class ApplicationTestCase {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        MockitoAnnotations.initMocks(this);
    }

    public Application getApplication() {
        return RuntimeEnvironment.application;
    }

    public Context getContext() {
        return RuntimeEnvironment.application;
    }

}
