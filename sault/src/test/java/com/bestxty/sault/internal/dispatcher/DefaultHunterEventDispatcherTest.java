package com.bestxty.sault.internal.dispatcher;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.dispatcher.handler.InternalEventDispatcherHandler;
import com.bestxty.sault.internal.handler.HunterEventHandler;
import com.bestxty.sault.internal.handler.TaskRequestEventHandler;
import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.task.SaultTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.shadows.ShadowLooper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest(Sault.class)
public class DefaultHunterEventDispatcherTest extends ApplicationTestCase {

    private HunterEventDispatcher hunterEventDispatcher;

    private TaskRequestEventDispatcher taskRequestEventDispatcher;

    @Mock
    private TaskRequestEventHandler taskRequestEventHandler;

    @Mock
    private HunterEventHandler hunterEventHandler;

    @Mock
    private TaskHunter taskHunter;

    @Mock
    private SaultTask task;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        Sault sault = PowerMockito.mock(Sault.class);

        PowerMockito.when(sault.isLoggingEnabled()).thenReturn(false);
        when(task.getSault()).thenReturn(sault);
        when(taskHunter.getSault()).thenReturn(sault);
        InternalEventDispatcherHandler handler = new InternalEventDispatcherHandler(ShadowLooper.myLooper(),
                taskRequestEventHandler, hunterEventHandler);
        hunterEventDispatcher = new DefaultHunterEventDispatcher(handler);
        taskRequestEventDispatcher = new DefaultHunterEventDispatcher(handler);
    }

    @Test
    public void dispatchHunterStart() throws Exception {
        hunterEventDispatcher.dispatchHunterStart(taskHunter);
        verify(hunterEventHandler, times(1)).handleHunterStart(taskHunter);
        verifyNoMoreInteractions(hunterEventHandler);

    }

    @Test
    public void dispatchHunterRetry() throws Exception {
        hunterEventDispatcher.dispatchHunterRetry(taskHunter);
        verify(hunterEventHandler, times(1)).handleHunterRetry(taskHunter);
        verifyNoMoreInteractions(hunterEventHandler);

    }

    @Test
    public void dispatchHunterException() throws Exception {
        hunterEventDispatcher.dispatchHunterException(taskHunter);
        verify(hunterEventHandler, times(1)).handleHunterException(taskHunter);
        verifyNoMoreInteractions(hunterEventHandler);

    }

    @Test
    public void dispatchHunterFinish() throws Exception {
        hunterEventDispatcher.dispatchHunterFinish(taskHunter);
        verify(hunterEventHandler, times(1)).handleHunterFinish(taskHunter);
        verifyNoMoreInteractions(hunterEventHandler);

    }

    @Test
    public void dispatchHunterFailed() throws Exception {
        hunterEventDispatcher.dispatchHunterFailed(taskHunter);
        verify(hunterEventHandler, times(1)).handleHunterFailed(taskHunter);
        verifyNoMoreInteractions(hunterEventHandler);

    }

    @Test
    public void dispatchSaultTaskSubmitRequest() throws Exception {
        taskRequestEventDispatcher.dispatchSaultTaskSubmitRequest(task);
        verify(taskRequestEventHandler, times(1)).handleSaultTaskSubmitRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskPauseRequest() throws Exception {
        taskRequestEventDispatcher.dispatchSaultTaskPauseRequest(task);
        verify(taskRequestEventHandler, times(1)).handleSaultTaskPauseRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskResumeRequest() throws Exception {
        taskRequestEventDispatcher.dispatchSaultTaskResumeRequest(task);
        verify(taskRequestEventHandler, times(1)).handleSaultTaskResumeRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskCancelRequest() throws Exception {
        taskRequestEventDispatcher.dispatchSaultTaskCancelRequest(task);
        verify(taskRequestEventHandler, times(1)).handleSaultTaskCancelRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }


}