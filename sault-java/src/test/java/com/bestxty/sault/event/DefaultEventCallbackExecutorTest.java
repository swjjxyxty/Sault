package com.bestxty.sault.event;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class DefaultEventCallbackExecutorTest {


    @Test
    public void execute() throws Exception {
        DefaultEventCallbackExecutor eventCallbackExecutor = new DefaultEventCallbackExecutor("Mock-Thread");
        @SuppressWarnings("unchecked") EventCallback<Event> callback = Mockito.mock(EventCallback.class);
        Event event = Mockito.mock(Event.class);
        ExecuteEventCallbackTask task = new ExecuteEventCallbackTask(callback, event);
        eventCallbackExecutor.execute(task);
        verify(callback, times(1)).onEvent(event);
    }

}