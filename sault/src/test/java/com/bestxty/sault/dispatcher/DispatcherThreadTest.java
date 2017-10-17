package com.bestxty.sault.dispatcher;

import com.bestxty.sault.ApplicationTestCase;

import org.junit.Before;
import org.junit.Test;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.bestxty.sault.Utils.DISPATCHER_THREAD_NAME;
import static org.junit.Assert.*;

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