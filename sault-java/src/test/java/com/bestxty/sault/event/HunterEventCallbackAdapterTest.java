package com.bestxty.sault.event;

import com.bestxty.sault.Hunter;
import com.bestxty.sault.downloader.HttpURLConnectionDownloader;
import com.bestxty.sault.event.hunter.HunterCancelEvent;
import com.bestxty.sault.event.hunter.HunterCompleteEvent;
import com.bestxty.sault.event.hunter.HunterPauseEvent;
import com.bestxty.sault.event.hunter.HunterProgressEvent;
import com.bestxty.sault.event.hunter.HunterResumeEvent;
import com.bestxty.sault.event.hunter.HunterStartEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class HunterEventCallbackAdapterTest {

    private HunterEventCallbackAdapter callbackAdapter;

    private HunterEventDispatcher eventDispatcher;

    @Mock
    private Hunter hunter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        callbackAdapter = Mockito.spy(new TaskEventDispatcher(new DefaultEventCallbackExecutor("Task-Mock-Thread")));
        eventDispatcher = new HunterEventDispatcher(new DefaultEventCallbackExecutor("Hunter-Mock-Thread"), null, new HttpURLConnectionDownloader());
        callbackAdapter.setHunterEventDispatcher(eventDispatcher);
    }

    @Test
    public void performHunterStart() throws Exception {

        HunterStartEvent startEvent = new HunterStartEvent(hunter);
        eventDispatcher.dispatcherEvent(startEvent);
        verify(callbackAdapter, times(1)).performHunterStart(startEvent);
    }

    @Test
    public void performHunterPause() throws Exception {
        HunterPauseEvent pauseEvent = new HunterPauseEvent(hunter);
        eventDispatcher.dispatcherEvent(pauseEvent);
        verify(callbackAdapter, times(1)).performHunterPause(pauseEvent);
    }

    @Test
    public void performHunterCancel() throws Exception {
        HunterCancelEvent cancelEvent = new HunterCancelEvent(hunter);
        eventDispatcher.dispatcherEvent(cancelEvent);
        verify(callbackAdapter, times(1)).performHunterCancel(cancelEvent);
    }

    @Test
    public void performHunterResume() throws Exception {
        HunterResumeEvent resumeEvent = new HunterResumeEvent(hunter);
        eventDispatcher.dispatcherEvent(resumeEvent);
        verify(callbackAdapter, times(1)).performHunterResume(resumeEvent);
    }

    @Test
    public void performHunterComplete() throws Exception {
        HunterCompleteEvent completeEvent = new HunterCompleteEvent(hunter);
        eventDispatcher.dispatcherEvent(completeEvent);
        verify(callbackAdapter, times(1)).performHunterComplete(completeEvent);
    }

    @Test
    public void performHunterProgress() throws Exception {
        HunterProgressEvent progressEvent = new HunterProgressEvent(hunter, 100);
        eventDispatcher.dispatcherEvent(progressEvent);
        verify(callbackAdapter, times(1)).performHunterProgress(progressEvent);
    }

}