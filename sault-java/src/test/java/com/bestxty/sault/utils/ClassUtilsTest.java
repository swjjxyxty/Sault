package com.bestxty.sault.utils;

import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.hunter.HunterStartEvent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class ClassUtilsTest {

    @Test
    public void getEventType() throws Exception {
        Class<?> clazz = ClassUtils.getEventType(new EventCallback<HunterStartEvent>() {
            @Override
            public void onEvent(HunterStartEvent event) {

            }
        });
        assertEquals(HunterStartEvent.class, clazz);
    }

    @Test
    public void getEventTypeWithLambada() throws Exception {
        Class<?> clazz = ClassUtils.getEventType(new EventCallback<Event>() {
            @Override
            public void onEvent(Event event) {

            }
        });
        assertNull(clazz);
    }

    @Test
    public void getEventTypeWithInnerClass() throws Exception {
        Class<?> clazz = ClassUtils.getEventType(new InternalCallback());
        assertEquals(HunterStartEvent.class, clazz);
    }

    private static final class InternalCallback implements EventCallback<HunterStartEvent> {
        @Override
        public void onEvent(HunterStartEvent event) {

        }
    }

}