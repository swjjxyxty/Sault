package com.bestxty.sunshine.bean;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class DefaultBeanFactory implements BeanFactory {


    @Override
    public Object getBean(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getBean(String className, List<Object> args) {
        Class<?>[] argsClass = this.getArgsClasses(args);
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = getConstructor(clazz, argsClass);
            return constructor.newInstance(args.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getConstructor(Class<?> clazz, Class<?>[] argsClass)
            throws NoSuchMethodException {
        Constructor<?> constructor = getProcessConstructor(clazz, argsClass);
        if (constructor == null) {
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> c : constructors) {
                Class<?>[] tempClass = c.getParameterTypes();
                if (tempClass.length == argsClass.length) {
                    if (isSameArgs(argsClass, tempClass)) {
                        return c;
                    }
                }
            }
        } else {
            return constructor;
        }
        throw new NoSuchMethodException("找不到指定的构造器");
    }

    private Constructor<?> getProcessConstructor(Class<?> clazz, Class<?>[] argsClass) {
        try {
            return clazz.getConstructor(argsClass);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Class<?>[] getArgsClasses(List<Object> args) {
        List<Class<?>> result = new ArrayList<>();
        for (Object arg : args) {
            result.add(getClass(arg));
        }
        Class<?>[] a = new Class[result.size()];
        return result.toArray(a);
    }

    private static Class<?> getClass(Object obj) {
        if (obj instanceof Integer) {
            return Integer.TYPE;
        } else if (obj instanceof Boolean) {
            return Boolean.TYPE;
        } else if (obj instanceof Long) {
            return Long.TYPE;
        } else if (obj instanceof Short) {
            return Short.TYPE;
        } else if (obj instanceof Double) {
            return Double.TYPE;
        } else if (obj instanceof Float) {
            return Float.TYPE;
        } else if (obj instanceof Character) {
            return Character.TYPE;
        } else if (obj instanceof Byte) {
            return Byte.TYPE;
        }
        return obj.getClass();
    }

    private boolean isSameArgs(Class<?>[] argsClass, Class<?>[] tempClass) {
        for (int i = 0; i < argsClass.length; i++) {
            try {
                argsClass[i].asSubclass(tempClass[i]);
                if (i == (argsClass.length - 1)) {
                    return true;
                }
            } catch (Exception e) {
                break;
            }
        }
        return false;
    }

}
