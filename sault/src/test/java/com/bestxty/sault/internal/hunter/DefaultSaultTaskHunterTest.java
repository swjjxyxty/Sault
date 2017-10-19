package com.bestxty.sault.internal.hunter;

import android.net.Uri;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Downloader;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.di.modules.HunterModule;
import com.bestxty.sault.internal.dispatcher.HunterEventDispatcher;
import com.bestxty.sault.internal.di.components.DaggerSaultComponent;
import com.bestxty.sault.internal.di.components.SaultComponent;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.internal.task.PartedSaultTask;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest({Utils.class, Sault.class, SaultModule.class,
        DefaultSaultTaskHunter.class, DaggerHunterComponent.class,
        DaggerHunterComponent.Builder.class})
public class DefaultSaultTaskHunterTest extends ApplicationTestCase {

    private DefaultSaultTaskHunter taskHunter;

    private Sault sault;

    @Mock
    private PartedSaultTask task;

    @Mock
    private Downloader downloader;

    @Mock
    private Downloader.Response response;

    private InputStream inputStream;

    @Mock
    private Uri uri;

    @Mock
    private HunterEventDispatcher hunterEventDispatcher;

    @Override
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


        taskHunter = new DefaultSaultTaskHunter(task);

        inputStream = new ByteArrayInputStream("test".getBytes(Charset.forName("UTF-8")));

        MemberModifier.field(DefaultSaultTaskHunter.class, "downloader").set(taskHunter, downloader);
        MemberModifier.field(Downloader.Response.class, "stream").set(response, inputStream);
        MemberModifier.field(DefaultSaultTaskHunter.class, "eventDispatcher").set(taskHunter, hunterEventDispatcher);
        when(task.getUri()).thenReturn(uri);
    }

    @Test
    public void hunterWithContentLengthException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 0);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);

        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunterWithStreamContentLengthException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "stream").set(response, null);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);

        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunterWithResponseException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenThrow(new Downloader.ResponseException("", 400));

        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunterWithInterruptedIOException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);
//        PowerMockito.when(task, "copyStream", inputStream,0L).thenThrow(new InterruptedIOException("test-exception"));
        MemberModifier.stub(MemberMatcher.method(DefaultSaultTaskHunter.class, "copyStream", InputStream.class, Long.TYPE))
                .toThrow(new InterruptedIOException("test-exception"));
        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunterWithIOException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);
//        PowerMockito.when(task, "copyStream", inputStream,0L).thenThrow(new InterruptedIOException("test-exception"));
        MemberModifier.stub(MemberMatcher.method(DefaultSaultTaskHunter.class, "copyStream", InputStream.class, Long.TYPE))
                .toThrow(new IOException("test-exception"));
        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunterWithException() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);
//        PowerMockito.when(task, "copyStream", inputStream,0L).thenThrow(new InterruptedIOException("test-exception"));
        MemberModifier.stub(MemberMatcher.method(DefaultSaultTaskHunter.class, "copyStream", InputStream.class, Long.TYPE))
                .toThrow(new Exception("test-exception"));
        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNotNull(taskHunter.getException());
        verify(task, never()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, never()).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, times(1)).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    @Test
    public void hunter() throws Exception {
        when(task.getStartPosition()).thenReturn(0L);
        when(task.getEndPosition()).thenReturn(1000L);
        MemberModifier.field(Downloader.Response.class, "contentLength").set(response, 1000);
        when(downloader.load(uri, 0, 1000)).thenReturn(response);

        File target = createDummyFile();
        Utils.createTargetFile(target);
        when(task.getTarget()).thenReturn(target);

        taskHunter.hunter();
        if (taskHunter.getException() != null) taskHunter.getException().printStackTrace();
        assertNull(taskHunter.getException());
        verify(task, atLeastOnce()).notifyFinishedSize(anyLong());
        verify(hunterEventDispatcher, times(1)).dispatchHunterFinish(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterFailed(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterRetry(taskHunter);
        verify(hunterEventDispatcher, never()).dispatchHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventDispatcher);
    }

    private File createDummyFile() {
        String dummyFilePath = RuntimeEnvironment.application.getCacheDir().getPath() + File.separator + "dummyFile";
        return new File(dummyFilePath);
    }

}