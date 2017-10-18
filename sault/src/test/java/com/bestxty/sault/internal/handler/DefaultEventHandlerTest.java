package com.bestxty.sault.internal.handler;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.NetworkStatusProvider;
import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.dispatcher.SaultTaskEventDispatcher;
import com.bestxty.sault.internal.hunter.MockPartingSaultTaskHunterProvider;
import com.bestxty.sault.internal.hunter.TaskHunter;
import com.bestxty.sault.internal.hunter.TaskHunterFactory;
import com.bestxty.sault.internal.task.ExceptionSaultTask;
import com.bestxty.sault.internal.task.SaultTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
@PrepareForTest({Utils.class, DefaultEventHandler.class, TaskHunterFactory.class})
public class DefaultEventHandlerTest extends ApplicationTestCase {

    private DefaultEventHandler defaultEventHandler;

    @Mock
    private ExecutorService executorService;

    @Mock
    private NetworkStatusProvider networkStatusProvider;

    @Mock
    private SaultTaskEventDispatcher saultTaskEventDispatcher;

    @Mock
    private TaskHunter taskHunter;

    @Mock
    private SaultTask task;

    @Mock
    private Future future;

    @Mock
    private Exception exception;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        defaultEventHandler = new DefaultEventHandler(executorService, networkStatusProvider, saultTaskEventDispatcher);
        when(taskHunter.getTask()).thenReturn(task);
    }

    @Test
    public void handleSaultTaskSubmitRequest() throws Exception {
        TaskHunter taskHunter = new MockPartingSaultTaskHunterProvider().mockPartingSaultTaskHunter();
        PowerMockito.mockStatic(TaskHunterFactory.class);
        PowerMockito.when(TaskHunterFactory.class, "newTaskHunter", task).thenReturn(taskHunter);
        when(executorService.submit(taskHunter)).thenReturn(future);
        defaultEventHandler.handleSaultTaskSubmitRequest(task);
        verify(executorService, times(1)).submit(taskHunter);
        verify(taskHunter, times(1)).setFuture(future);
    }

    @Test
    public void handleSaultTaskPauseRequest() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(null);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "getHunterSequences", SaultTask.class))
                .toReturn(Collections.singletonList(1));
        List<SaultTask> canceledTask = Collections.singletonList(task);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "cancelHunters", List.class))
                .toReturn(canceledTask);
        defaultEventHandler.handleSaultTaskPauseRequest(task);

        verify(pausedTaskMap, times(1)).put("test-key", canceledTask);
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskPause(task);
    }

    @Test
    public void handleSaultTaskPauseRequestWithPausedTask() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(Collections.singletonList(task));
        defaultEventHandler.handleSaultTaskPauseRequest(task);
        verify(pausedTaskMap, times(1)).get("test-key");
        verify(pausedTaskMap, never()).put(Matchers.any(), Matchers.any());
        verifyZeroInteractions(saultTaskEventDispatcher);
    }

    @Test
    public void handleSaultTaskResumeRequestWithNoPauseTask() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(null);
        defaultEventHandler.handleSaultTaskResumeRequest(task);

        verify(pausedTaskMap, never()).remove("test-key");
        verifyZeroInteractions(saultTaskEventDispatcher);
    }

    @Test
    public void handleSaultTaskResumeRequest() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(Collections.singletonList(task));

        TaskHunter taskHunter = new MockPartingSaultTaskHunterProvider().mockPartingSaultTaskHunter();
        PowerMockito.mockStatic(TaskHunterFactory.class);
        PowerMockito.when(TaskHunterFactory.class, "newTaskHunter", task).thenReturn(taskHunter);

        when(executorService.submit(taskHunter)).thenReturn(future);
//        defaultEventHandler.handleSaultTaskSubmitRequest(task);
        defaultEventHandler.handleSaultTaskResumeRequest(task);

        verify(executorService, times(1)).submit(taskHunter);
        verify(taskHunter, times(1)).setFuture(future);
        verify(pausedTaskMap, times(1)).remove("test-key");
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskResume(task);
    }

    @Test
    public void handleSaultTaskCancelRequestWithPausedTask() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(Collections.singletonList(task));
        defaultEventHandler.handleSaultTaskCancelRequest(task);
        verify(pausedTaskMap, times(1)).remove("test-key");
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskCancel(task);
    }

    @Test
    public void handleSaultTaskCancelRequest() throws Exception {
        Map pausedTaskMap = Mockito.mock(HashMap.class);
        MemberModifier.field(DefaultEventHandler.class, "pausedTaskMap").set(defaultEventHandler, pausedTaskMap);
        when(task.getKey()).thenReturn("test-key");
        when(pausedTaskMap.get("test-key")).thenReturn(null);

        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "getHunterSequences", SaultTask.class))
                .toReturn(Collections.singletonList(1));
        List canceledTask = Mockito.mock(List.class);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "cancelHunters", List.class))
                .toReturn(canceledTask);

        defaultEventHandler.handleSaultTaskCancelRequest(task);

        verify(canceledTask, times(1)).clear();
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskCancel(task);
    }

    @Test
    public void handleHunterStart() throws Exception {
        defaultEventHandler.handleHunterStart(taskHunter);
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskStart(task);
        verifyNoMoreInteractions(saultTaskEventDispatcher);
    }

    @Test
    public void handleHunterRetry() throws Exception {

        when(taskHunter.isCancelled()).thenReturn(false);
        when(executorService.isShutdown()).thenReturn(false);

        when(networkStatusProvider.accessNetwork()).thenReturn(true);
        NetworkInfo networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_MOBILE, TelephonyManager.NETWORK_TYPE_LTE, true, true);

        when(taskHunter.shouldRetry(false, networkInfo)).thenReturn(true);
        when(networkStatusProvider.getNetworkInfo()).thenReturn(networkInfo);
        when(networkStatusProvider.isAirplaneMode()).thenReturn(false);


        //noinspection unchecked
        when(executorService.submit(taskHunter)).thenReturn(future);

        defaultEventHandler.handleHunterRetry(taskHunter);
        verify(taskHunter, times(1)).isCancelled();
        verify(executorService, times(1)).isShutdown();
        verify(networkStatusProvider, times(2)).accessNetwork();
        verify(networkStatusProvider, times(1)).getNetworkInfo();
        verify(taskHunter, times(1)).shouldRetry(false, networkInfo);
        verify(executorService, times(1)).submit(taskHunter);
        verify(taskHunter, times(1)).setFuture(future);
    }

    @Test
    public void handleHunterRetryWithCanceledHunter() throws Exception {
        when(taskHunter.isCancelled()).thenReturn(true);
        defaultEventHandler.handleHunterRetry(taskHunter);
        verify(taskHunter, times(1)).isCancelled();
        verifyNoMoreInteractions(taskHunter);
        verifyZeroInteractions(executorService, networkStatusProvider);
    }

    @Test
    public void handleHunterRetryWithShutdownExecutorService() throws Exception {

        when(taskHunter.isCancelled()).thenReturn(false);
        when(executorService.isShutdown()).thenReturn(true);
        when(task.getKey()).thenReturn("");
        when(taskHunter.getException()).thenReturn(exception);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "getHunterSequences")).toReturn(Collections.emptyList());
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "cancelHunters")).toReturn(Collections.emptyList());
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(task)).thenReturn("test-key");
        ArgumentCaptor<ExceptionSaultTask> argumentCaptor = ArgumentCaptor.forClass(ExceptionSaultTask.class);
        defaultEventHandler.handleHunterRetry(taskHunter);
        verify(taskHunter, times(1)).getTask();
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskException(argumentCaptor.capture());

        verify(taskHunter, times(1)).isCancelled();
        verify(executorService, times(1)).isShutdown();
    }

    @Test
    public void handleHunterException() throws Exception {
        when(taskHunter.isCancelled()).thenReturn(false);
        defaultEventHandler.handleHunterException(taskHunter);
        verify(taskHunter, times(1)).isCancelled();
        verifyNoMoreInteractions(taskHunter);
    }

    @Test
    public void handleHunterExceptionWithCanceledHunter() throws Exception {
        when(taskHunter.isCancelled()).thenReturn(true);

        when(task.getKey()).thenReturn("");
        when(taskHunter.getException()).thenReturn(exception);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "getHunterSequences")).toReturn(Collections.emptyList());
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "cancelHunters")).toReturn(Collections.emptyList());
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(task)).thenReturn("test-key");
        ArgumentCaptor<ExceptionSaultTask> argumentCaptor = ArgumentCaptor.forClass(ExceptionSaultTask.class);
        defaultEventHandler.handleHunterException(taskHunter);

        verify(taskHunter, times(1)).isCancelled();
        verify(taskHunter, times(1)).getTask();
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskException(argumentCaptor.capture());

    }

    @Test
    public void handleHunterFinishWithPartingSaultTaskHunter() throws Exception {
        PowerMockito.mockStatic(TaskHunterFactory.class);
        PowerMockito.when(TaskHunterFactory.class, "isPartingSaultTaskHunter", taskHunter).thenReturn(true);
        defaultEventHandler.handleHunterFinish(taskHunter);
        verify(taskHunter, never()).getTask();
    }

    @Test
    public void handleHunterFinishWithUnFinishedProgress() throws Exception {
        SaultTask.Progress progress = new SaultTask.Progress(100, 10);
        when(task.getProgress()).thenReturn(progress);

        defaultEventHandler.handleHunterFinish(taskHunter);

        verify(taskHunter, times(1)).getTask();
        verify(task, times(1)).getProgress();
    }

    @Test
    public void handleHunterFinishWithFinishedProgress() throws Exception {
        SaultTask.Progress progress = new SaultTask.Progress(100, 100);
        when(task.getProgress()).thenReturn(progress);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        defaultEventHandler.handleHunterFinish(taskHunter);

        verify(taskHunter, times(1)).getTask();
        verify(task, times(1)).getProgress();
        verify(task, times(1)).setEndTime(argumentCaptor.capture());
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskComplete(task);
    }

    @Test
    public void handleHunterFailedWithCanceledHunter() throws Exception {
        when(taskHunter.isCancelled()).thenReturn(true);
        defaultEventHandler.handleHunterFailed(taskHunter);
        verify(taskHunter, times(1)).isCancelled();
        verifyNoMoreInteractions(taskHunter);
    }

    @Test
    public void handleHunterFailed() throws Exception {
        when(taskHunter.isCancelled()).thenReturn(false);

        when(task.getKey()).thenReturn("");
        when(taskHunter.getException()).thenReturn(exception);
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "getHunterSequences")).toReturn(Collections.emptyList());
        MemberModifier.stub(MemberMatcher.method(DefaultEventHandler.class, "cancelHunters")).toReturn(Collections.emptyList());
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(task)).thenReturn("test-key");
        ArgumentCaptor<ExceptionSaultTask> argumentCaptor = ArgumentCaptor.forClass(ExceptionSaultTask.class);
        defaultEventHandler.handleHunterFailed(taskHunter);
        verify(taskHunter, times(1)).isCancelled();
        verify(taskHunter, times(1)).getTask();
        verify(saultTaskEventDispatcher, times(1)).dispatchSaultTaskException(argumentCaptor.capture());
    }

}