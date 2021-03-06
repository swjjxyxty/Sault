package com.bestxty.sault.internal.hunter;

import android.net.Uri;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.di.components.SaultComponent;
import com.bestxty.sault.internal.di.modules.HunterModule;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.internal.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.internal.dispatcher.TaskRequestEventDispatcher;
import com.bestxty.sault.internal.task.PartedSaultTask;
import com.bestxty.sault.internal.task.SaultTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest({Sault.class, SaultModule.class, Utils.class, DaggerHunterComponent.class, DaggerHunterComponent.Builder.class})
public class PartingSaultTaskHunterTest extends ApplicationTestCase {

    private PartingSaultTaskHunter taskHunter;

    @Mock
    private SaultTask task;

    private Sault sault;

    @Mock
    private HunterEventDispatcher hunterEventDispatcher;

    @Mock
    private TaskRequestEventDispatcher taskRequestEventDispatcher;

    @Mock
    private Uri uri;

    @Mock
    private Downloader downloader;

    @Mock
    private HunterEventDispatcher defaultHunterEventDispatcher;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        sault = PowerMockito.mock(Sault.class);
        when(task.getSault()).thenReturn(sault);
        SaultComponent saultComponent = Mockito.mock(SaultComponent.class);
        when(sault.getSaultComponent()).thenReturn(saultComponent);

        DaggerHunterComponent hunterComponent = Mockito.mock(DaggerHunterComponent.class);
        DaggerHunterComponent.Builder builder = Mockito.mock(DaggerHunterComponent.Builder.class);
        PowerMockito.mockStatic(DaggerHunterComponent.class);

        PowerMockito.when(DaggerHunterComponent.builder()).thenReturn(builder);
        PowerMockito.when(builder, MemberMatcher.method(DaggerHunterComponent.Builder.class, "hunterModule", HunterModule.class))
                .withArguments(Matchers.any())
                .thenReturn(builder);
        PowerMockito.when(builder, MemberMatcher.method(DaggerHunterComponent.Builder.class, "saultComponent", SaultComponent.class))
                .withArguments(Matchers.any())
                .thenReturn(builder);
        PowerMockito.when(builder, MemberMatcher.method(DaggerHunterComponent.Builder.class, "build"))
                .withNoArguments()
                .thenReturn(hunterComponent);


        taskHunter = new PartingSaultTaskHunter(task);
        MemberModifier.field(PartingSaultTaskHunter.class, "eventDispatcher").set(taskHunter, hunterEventDispatcher);
        MemberModifier.field(PartingSaultTaskHunter.class, "taskRequestEventDispatcher").set(taskHunter, taskRequestEventDispatcher);
        MemberModifier.field(PartingSaultTaskHunter.class, "downloader").set(taskHunter, downloader);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.whenNew(PartedSaultTask.class).withArguments(any(SaultTask.class), anyLong(), anyLong())
                .thenReturn(Mockito.mock(PartedSaultTask.class));
    }

    @After
    public void tearDown() throws Exception {
        boolean ignoredResult = createDummyFile().delete();
        System.out.println("ignoredResult = " + ignoredResult);
    }

    @Test
    public void getException() throws Exception {
        File target = createDummyFile();

        when(task.getTarget()).thenReturn(target);
        when(task.getUri()).thenReturn(uri);
        when(downloader.fetchContentLength(uri)).thenReturn(0L);

        taskHunter.hunter();
        verify(hunterEventDispatcher, times(1)).dispatchHunterStart(taskHunter);
        assertThat(target.exists(), is(false));
        assertNotNull(taskHunter.getException());
        assertTrue(taskHunter.getException() instanceof Downloader.ContentLengthException);
        verify(hunterEventDispatcher, times(1)).dispatchHunterException(taskHunter);
    }

    @Test
    public void hunter() throws Exception {
        File target = createDummyFile();

        when(task.getTarget()).thenReturn(target);
        when(task.getUri()).thenReturn(uri);
        when(downloader.fetchContentLength(uri)).thenReturn(1000L);

        taskHunter.hunter();
        verify(hunterEventDispatcher, times(1)).dispatchHunterStart(taskHunter);
        assertThat(target.exists(), is(true));
        assertNull(taskHunter.getException());
        verify(taskRequestEventDispatcher, atLeastOnce()).dispatchSaultTaskSubmitRequest(((SaultTask) anyObject()));
        verify(hunterEventDispatcher, times(1)).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
    }

    private File createDummyFile() {
        String dummyFilePath = RuntimeEnvironment.application.getCacheDir().getPath() + File.separator + "dummyFile";
        return new File(dummyFilePath);
    }

}