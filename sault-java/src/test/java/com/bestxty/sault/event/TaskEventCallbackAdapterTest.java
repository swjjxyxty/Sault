package com.bestxty.sault.event;

import com.bestxty.sault.Task;
import com.bestxty.sault.downloader.HttpURLConnectionDownloader;
import com.bestxty.sault.event.task.TaskCancelEvent;
import com.bestxty.sault.event.task.TaskCompleteEvent;
import com.bestxty.sault.event.task.TaskPauseEvent;
import com.bestxty.sault.event.task.TaskResumeEvent;
import com.bestxty.sault.event.task.TaskSubmitEvent;

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
public class TaskEventCallbackAdapterTest {

    private TaskEventCallbackAdapter callbackAdapter;

    private TaskEventDispatcher taskEventDispatcher;

    @Mock
    private Task task;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        callbackAdapter = Mockito.spy(new HunterEventDispatcher(new DefaultEventCallbackExecutor("Hunter-Mock-Thread"), null, new HttpURLConnectionDownloader()));
        taskEventDispatcher = new TaskEventDispatcher(new DefaultEventCallbackExecutor("Task-Mock-Thread"));
        callbackAdapter.setTaskEventDispatcher(taskEventDispatcher);

    }

    @Test
    public void performTaskSubmit() throws Exception {
        TaskSubmitEvent event = new TaskSubmitEvent(task);
        taskEventDispatcher.dispatcherEvent(event);
        verify(callbackAdapter, times(1)).performTaskSubmit(event);
    }

    @Test
    public void performTaskPause() throws Exception {
        TaskPauseEvent event = new TaskPauseEvent(task);
        taskEventDispatcher.dispatcherEvent(event);
        verify(callbackAdapter, times(1)).performTaskPause(event);
    }

    @Test
    public void performTaskCancel() throws Exception {
        TaskCancelEvent event = new TaskCancelEvent(task);
        taskEventDispatcher.dispatcherEvent(event);
        verify(callbackAdapter, times(1)).performTaskCancel(event);
    }

    @Test
    public void performTaskResume() throws Exception {
        TaskResumeEvent event = new TaskResumeEvent(task);
        taskEventDispatcher.dispatcherEvent(event);
        verify(callbackAdapter, times(1)).performTaskResume(event);
    }

    @Test
    public void performTaskComplete() throws Exception {
        TaskCompleteEvent event = new TaskCompleteEvent(task);
        taskEventDispatcher.dispatcherEvent(event);
        verify(callbackAdapter, times(1)).performTaskComplete(event);
    }

}