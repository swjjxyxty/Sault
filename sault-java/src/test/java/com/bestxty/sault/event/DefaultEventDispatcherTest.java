package com.bestxty.sault.event;

import com.bestxty.sault.event.hunter.HunterEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class DefaultEventDispatcherTest {

    private SimpleEventDispatcher defaultEventDispatcher;

    @Before
    public void setUp() {
        defaultEventDispatcher = new SimpleEventDispatcher(new DefaultEventCallbackExecutor("Mock-Thread"));
    }

    @After
    public void clear() {
        defaultEventDispatcher.removeAllCallbacks();
    }

    @Test
    public void addEventCallback() throws Exception {
        defaultEventDispatcher.addEventCallback(new EventCallback<HunterEvent>() {
            @Override
            public void onEvent(HunterEvent event) {

            }
        });
        assertEquals(1, defaultEventDispatcher.callbackSize());
    }

    @Test
    public void removeEventCallback() throws Exception {
        EventCallback<HunterEvent> callback = new EventCallback<HunterEvent>() {
            @Override
            public void onEvent(HunterEvent event) {

            }
        };
        defaultEventDispatcher.addEventCallback(callback);
        assertEquals(1, defaultEventDispatcher.callbackSize());
        defaultEventDispatcher.removeEventCallback(callback);
        assertEquals(0, defaultEventDispatcher.callbackSize());
    }

    @Test
    public void removeAllCallbacks() throws Exception {
        defaultEventDispatcher.addEventCallback(new EventCallback<HunterEvent>() {
            @Override
            public void onEvent(HunterEvent event) {

            }
        });
        defaultEventDispatcher.addEventCallback(new EventCallback<HunterEvent>() {
            @Override
            public void onEvent(HunterEvent event) {

            }
        });
        assertEquals(2, defaultEventDispatcher.callbackSize());
        defaultEventDispatcher.removeAllCallbacks();
        assertEquals(0, defaultEventDispatcher.callbackSize());
    }


}