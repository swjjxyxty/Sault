package com.bestxty.sault.internal.dispatcher;

import com.bestxty.sault.ApplicationTestCase;

import org.junit.Before;
import org.junit.Test;

import static com.bestxty.sault.internal.Utils.DISPATCHER_THREAD_NAME;
import static org.junit.Assert.assertEquals;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/17.
 */
public class DispatcherThreadTest extends ApplicationTestCase {


    private DispatcherThread dispatcherThread;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dispatcherThread = new DispatcherThread();
    }

    @Test
    public void testThreadName() throws Exception {
        assertEquals(DISPATCHER_THREAD_NAME, dispatcherThread.getName());
    }


}