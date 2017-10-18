package com.bestxty.sault.internal.dispatcher.handler;

import android.os.Handler;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.handler.SaultTaskEventHandler;
import com.bestxty.sault.internal.task.SaultTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.shadows.ShadowLooper;

import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_CANCEL;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_COMPLETE;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_EXCEPTION;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_PAUSE;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_PROGRESS;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_RESUME;
import static com.bestxty.sault.internal.handler.SaultTaskEventHandler.SAULT_TASK_START;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest(Sault.class)
public class InternalMainThreadHandlerTest extends ApplicationTestCase {
    private Handler mainThreadHandler;

    @Mock
    private SaultTaskEventHandler taskEventHandler;

    @Mock
    private SaultTask task;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Sault sault = PowerMockito.mock(Sault.class);

        PowerMockito.when(sault.isLoggingEnabled()).thenReturn(false);
        when(task.getSault()).thenReturn(sault);
        mainThreadHandler = new InternalMainThreadHandler(ShadowLooper.getMainLooper(), taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskStart() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_START, task));
        verify(taskEventHandler, times(1)).handleSaultTaskStart(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskPause() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_PAUSE, task));
        verify(taskEventHandler, times(1)).handleSaultTaskPause(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskResume() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_RESUME, task));
        verify(taskEventHandler, times(1)).handleSaultTaskResume(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskCancel() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_CANCEL, task));
        verify(taskEventHandler, times(1)).handleSaultTaskCancel(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskComplete() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_COMPLETE, task));
        verify(taskEventHandler, times(1)).handleSaultTaskComplete(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskProgress() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_PROGRESS, task));
        verify(taskEventHandler, times(1)).handleSaultTaskProgress(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

    @Test
    public void dispatchSaultTaskException() {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(SAULT_TASK_EXCEPTION, task));
        verify(taskEventHandler, times(1)).handleSaultTaskException(task);
        verifyNoMoreInteractions(taskEventHandler);
    }

}