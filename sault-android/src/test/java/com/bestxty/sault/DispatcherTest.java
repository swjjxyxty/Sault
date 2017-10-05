package com.bestxty.sault;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */
public class DispatcherTest extends ApplicationTestCase {

    private final ExecutorService executorService = new SaultExecutorService() {
        @Override
        public Future<?> submit(Runnable task) {
            FTask ft = new FTask(task);
            execute(ft);
            return ft;
        }

        class FTask extends FutureTask<Runnable> {
            FTask(Runnable r) {
                super(r, null);
            }
        }
    };

    private Dispatcher dispatcher;


    @Before
    public void setupDispatcher() {
        Downloader downloader = Mockito.mock(Downloader.class);
        dispatcher = new Dispatcher(getContext(), executorService, Sault.HANDLER, downloader, false);
    }

    @Test
    public void isAutoAdjustThreadEnabled() throws Exception {
        assertFalse(dispatcher.isAutoAdjustThreadEnabled());
    }

    @Test
    public void shutdown() throws Exception {
        dispatcher.shutdown();
        assertTrue(executorService.isShutdown());
    }

    @Test
    public void submit() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        dispatcher.submit(runnable);
        verify(runnable, times(1)).run();
    }

    @Test
    public void dispatchProgress() throws Exception {
    }

    @Test
    public void dispatchSubmit() throws Exception {

    }

    @Test
    public void dispatchPauseTag() throws Exception {

    }

    @Test
    public void dispatchCancel() throws Exception {

    }

    @Test
    public void dispatchResumeTag() throws Exception {

    }

    @Test
    public void dispatchComplete() throws Exception {

    }

    @Test
    public void dispatchFailed() throws Exception {

    }

    @Test
    public void dispatchRetry() throws Exception {

    }

}