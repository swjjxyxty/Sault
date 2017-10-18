package com.bestxty.sault.internal.task;

import com.bestxty.sault.ApplicationTestCase;
import com.bestxty.sault.Sault;
import com.bestxty.sault.internal.Utils;
import com.bestxty.sault.internal.di.components.SaultComponent;
import com.bestxty.sault.internal.di.modules.SaultModule;
import com.bestxty.sault.internal.dispatcher.SaultTaskEventDispatcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */
@PrepareForTest({Utils.class, Sault.class, SaultModule.class, PartedSaultTask.class})
public class PartedSaultTaskTest extends ApplicationTestCase {
    @Mock
    private SaultTaskEventDispatcher taskEventDispatcher;

    @Mock
    private SaultTask task;

    private Sault sault;

    private SaultModule saultModule;

    private PartedSaultTask partedSaultTask;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        sault = PowerMockito.mock(Sault.class);
        saultModule = PowerMockito.mock(SaultModule.class);

        SaultComponent saultComponent = Mockito.mock(SaultComponent.class);

        when(task.getSault()).thenReturn(sault);
        when(sault.getSaultComponent()).thenReturn(saultComponent);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(task)).thenReturn("test-key");
        PowerMockito.when(Utils.generateTaskKey(partedSaultTask)).thenReturn("test-key");


        partedSaultTask = new PartedSaultTask(task, 0, 1000);
        MemberModifier.field(PartedSaultTask.class, "eventDispatcher")
                .set(partedSaultTask, taskEventDispatcher);

    }

    @Test
    public void getProgress() throws Exception {
        Exception exception = null;
        try {
            partedSaultTask.getProgress();
        } catch (UnsupportedOperationException e) {
            exception = e;
        }
        assertNotNull(exception);
    }


    @Test
    public void getStartPosition() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.generateTaskKey(task))
                .thenReturn("test-key");
        assertEquals(0, partedSaultTask.getStartPosition());
        partedSaultTask.notifyFinishedSize(1000);
        assertEquals(1000, partedSaultTask.getStartPosition());
    }

}