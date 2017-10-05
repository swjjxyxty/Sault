package com.bestxty.sault.utils;

import com.bestxty.sault.event.EventCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public final class ClassUtils {

    public static Class<?> getEventType(EventCallback<?> callback) {
        Class<?> callbackClass = callback.getClass();
        Type[] genericInterfaces = callbackClass.getGenericInterfaces();
        Type eventType = genericInterfaces[0];
        if (eventType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) eventType).getActualTypeArguments()[0];
        }

        return null;
    }
}
