package com.bestxty.sault.context;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */

public final class SaultContext {

    private final Map<String, Class<?>> classMap = new HashMap<>();

    public SaultContext() {

    }

    public <T> T getBean(Class<T> tClass) throws NoSuchMethodException {
        Class<?> objectClass = classMap.get(tClass.getCanonicalName());
        Constructor<?>[] constructors = objectClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            constructor.getParameterTypes();
        }

        return null;
    }
}
