package com.bestxty.sault.handler;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Callback;
import com.bestxty.sault.SaultException;
import com.bestxty.sault.task.ExceptionSaultTask;
import com.bestxty.sault.task.SaultTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
public class DefaultSaultTaskEventHandlerTest extends ApplicationTestCase {

    private DefaultSaultTaskEventHandler saultTaskEventHandler;

    @Mock
    private SaultTask task;

    @Mock
    private Object tag;

    @Mock
    private Callback callback;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        saultTaskEventHandler = new DefaultSaultTaskEventHandler();
        when(task.getTag()).thenReturn(tag);
        when(task.getCallback()).thenReturn(callback);
    }

    @Test
    public void handleSaultTaskStart() throws Exception {
        saultTaskEventHandler.handleSaultTaskStart(task);
        verify(callback, times(1)).onEvent(tag, Callback.EVENT_START);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskStart(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskPause() throws Exception {
        saultTaskEventHandler.handleSaultTaskPause(task);
        verify(callback, times(1)).onEvent(tag, Callback.EVENT_PAUSE);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskPause(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskResume() throws Exception {
        saultTaskEventHandler.handleSaultTaskResume(task);
        verify(callback, times(1)).onEvent(tag, Callback.EVENT_RESUME);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskResume(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskCancel() throws Exception {
        saultTaskEventHandler.handleSaultTaskCancel(task);
        verify(callback, times(1)).onEvent(tag, Callback.EVENT_CANCEL);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskCancel(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskComplete() throws Exception {
        saultTaskEventHandler.handleSaultTaskComplete(task);
        verify(callback, times(1)).onEvent(tag, Callback.EVENT_COMPLETE);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskComplete(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskProgress() throws Exception {
        SaultTask.Progress progress = new SaultTask.Progress(100, 100);
        when(task.getProgress()).thenReturn(progress);
        saultTaskEventHandler.handleSaultTaskProgress(task);
        verify(callback, times(1)).onProgress(tag, 100, 100);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskProgress(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskException() throws Exception {
        saultTaskEventHandler.handleSaultTaskException(task);
        verifyZeroInteractions(callback);

        when(task.getCallback()).thenReturn(null);
        saultTaskEventHandler.handleSaultTaskException(task);
        verifyZeroInteractions(callback);
    }

    @Test
    public void handleSaultTaskExceptionWithExceptionSaultTask() throws Exception {
        ArgumentCaptor<SaultException> argumentCaptor = ArgumentCaptor.forClass(SaultException.class);
        ExceptionSaultTask saultTask = Mockito.mock(ExceptionSaultTask.class);
        when(saultTask.getException()).thenReturn(Mockito.mock(Exception.class));
        when(saultTask.getCallback()).thenReturn(callback);

        saultTaskEventHandler.handleSaultTaskException(saultTask);
        verify(callback, times(1)).onError(argumentCaptor.capture());

    }

}