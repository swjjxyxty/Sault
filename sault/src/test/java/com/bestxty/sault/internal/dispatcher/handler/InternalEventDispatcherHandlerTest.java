package com.bestxty.sault.internal.dispatcher.handler;

import android.os.Handler;

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

import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_EXCEPTION;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_FAILED;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_FINISH;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_RETRY;
import static com.bestxty.sault.internal.handler.HunterEventHandler.HUNTER_START;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_CANCEL_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_PAUSE_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_RESUME_REQUEST;
import static com.bestxty.sault.internal.handler.TaskRequestEventHandler.TASK_SUBMIT_REQUEST;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest(Sault.class)
public class InternalEventDispatcherHandlerTest extends ApplicationTestCase {

    private Handler hunterHandler;

    @Mock
    private TaskRequestEventHandler taskRequestEventHandler;

    @Mock
    private HunterEventHandler hunterEventHandler;

    @Mock
    private TaskHunter hunter;

    @Mock
    private SaultTask task;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Sault sault = PowerMockito.mock(Sault.class);

        PowerMockito.when(sault.isLoggingEnabled()).thenReturn(false);
        when(task.getSault()).thenReturn(sault);
        when(hunter.getSault()).thenReturn(sault);

        hunterHandler = new InternalEventDispatcherHandler(ShadowLooper.myLooper(), taskRequestEventHandler, hunterEventHandler);
    }

    @Test
    public void dispatchHunterStart() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_START, hunter));
        verify(hunterEventHandler, times(1)).handleHunterStart(hunter);
        verifyNoMoreInteractions(hunterEventHandler);
    }

    @Test
    public void dispatchHunterRetry() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_RETRY, hunter));
        verify(hunterEventHandler, times(1)).handleHunterRetry(hunter);
        verifyNoMoreInteractions(hunterEventHandler);
    }

    @Test
    public void dispatchHunterException() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_EXCEPTION, hunter));
        verify(hunterEventHandler, times(1)).handleHunterException(hunter);
        verifyNoMoreInteractions(hunterEventHandler);
    }

    @Test
    public void dispatchHunterFinish() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_FINISH, hunter));
        verify(hunterEventHandler, times(1)).handleHunterFinish(hunter);
        verifyNoMoreInteractions(hunterEventHandler);
    }

    @Test
    public void dispatchHunterFailed() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(HUNTER_FAILED, hunter));
        verify(hunterEventHandler, times(1)).handleHunterFailed(hunter);
        verifyNoMoreInteractions(hunterEventHandler);
    }


    @Test
    public void dispatchSaultTaskSubmitRequest() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_SUBMIT_REQUEST, task));
        verify(taskRequestEventHandler, times(1)).handleSaultTaskSubmitRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskPauseRequest() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_PAUSE_REQUEST, task));
        verify(taskRequestEventHandler, times(1)).handleSaultTaskPauseRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskResumeRequest() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_RESUME_REQUEST, task));
        verify(taskRequestEventHandler, times(1)).handleSaultTaskResumeRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }

    @Test
    public void dispatchSaultTaskCancelRequest() {
        hunterHandler.sendMessage(hunterHandler.obtainMessage(TASK_CANCEL_REQUEST, task));
        verify(taskRequestEventHandler, times(1)).handleSaultTaskCancelRequest(task);
        verifyNoMoreInteractions(taskRequestEventHandler);
    }


}