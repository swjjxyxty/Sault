package com.bestxty.sault.task;

import com.bestxty.sault.Utils;
import com.bestxty.sault.dispatcher.SaultTaskEventDispatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class PartedSaultTaskTest {

    @Mock
    private SaultTaskEventDispatcher taskEventDispatcher;

    @Mock
    private SaultTask saultTask;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getStartPosition() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(saultTask))
                .thenReturn("test-key");
        PartedSaultTask task = new PartedSaultTask(saultTask, 1000, 10000);
        assertEquals(1000, task.getStartPosition());
        task.notifyFinishedSize(1000);
        assertEquals(2000, task.getStartPosition());
    }

}