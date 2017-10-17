package com.bestxty.sunshine.context;

import com.bestxty.sunshine.annotation.Autowired;
import com.bestxty.sunshine.bean.BeanDefinition;
import com.bestxty.sunshine.bean.BeanDefinitionFactory;
import com.bestxty.sunshine.bean.BeanFactory;
import com.bestxty.sunshine.bean.BeanWrapper;
import com.bestxty.sunshine.bean.ConstructorParameter;
import com.bestxty.sunshine.bean.DefaultBeanFactory;
import com.bestxty.sunshine.bean.InjectField;
import com.bestxty.sunshine.exception.BeanCreateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public abstract class AbstractSunshineContext implements SunshineContext {

    private Map<String, BeanWrapper> beansMap = new ConcurrentHashMap<>();

    private final Map<String, Class<?>> beanClassCache = new ConcurrentHashMap<>(16);

    private BeanDefinitionFactory beanDefinitionFactory;

    private BeanFactory beanFactory = new DefaultBeanFactory();

    public AbstractSunshineContext(BeanDefinitionFactory beanDefinitionFactory) {
        this.beanDefinitionFactory = beanDefinitionFactory;
    }

    @Override
    public void close() {
        beansMap.clear();
    }

    @Override
    public Object getBean(String id) {
        System.out.println("id--- = " + id);
        BeanWrapper wrapper = beansMap.get(id);
        if (wrapper == null) {
            wrapper = handleBean(id);
        }
        return wrapper.getBean();
    }

    private BeanWrapper handleBean(String id) {
        BeanWrapper wrapper = createBeanInstance(id);
        if (wrapper.isSingleton()) {
            beansMap.put(id, wrapper);
        }
        return wrapper;
    }

    private BeanWrapper createBeanInstance(String id) {
        BeanDefinition definition = beanDefinitionFactory.getBeanDefinition(id);
        if (definition == null) {
            throw new RuntimeException();
        }
        BeanWrapper wrapper;
        if (definition.getFactoryMethodName() != null) {
            wrapper = instantiateUsingFactoryMethod(definition);
        } else {
            Object bean = instanceBeanDefinition(definition);
            wrapper = new BeanWrapper(bean);
        }
        wrapper.setSingleton(definition.isSingleton());

//        for (InjectField injectField : definition.getInjectFields()) {
//            inject(wrapper.getBean(), injectField);
//        }

        return wrapper;
    }

    private BeanWrapper instantiateUsingFactoryMethod(BeanDefinition definition) {
        Object factoryBean;
        Class<?> factoryClass;
        if (definition.getFactoryBeanName() != null) {
            factoryBean = getBean(definition.getFactoryBeanName());
            factoryClass = factoryBean.getClass();
        } else {
            factoryBean = null;
            factoryClass = getBeanClass(definition.getBeanClassName());
        }

        try {
            Method method = factoryClass.getDeclaredMethod(definition.getFactoryMethodName());
            Object bean = method.invoke(factoryBean);
            return new BeanWrapper(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Object instanceBeanDefinition(BeanDefinition definition) {
        Class<?> beanClass = getBeanClass(definition.getBeanClassName());

        Constructor<?> constructor = getConstructor(beanClass);

        List<Object> args = getConstructArgs(constructor);

        return instantiateClass(constructor, args);
    }

    private List<Object> getConstructArgs(Constructor<?> constructor) {
        List<Object> args = new ArrayList<>();
//        List<ConstructorParameter> parameters = definition.getConstructorParameters();
//        for (ConstructorParameter parameter : parameters) {
//            args.add(getBean(parameter.getName()));
//        }

        return args;
    }

    private Object invokeMethod(Object obj, String methodName) {
        Class<?> objClass = obj.getClass();
        try {
            Method method = objClass.getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void inject(Object target, InjectField injectField) {
        Class<?> targetClass = target.getClass();
        try {
            Field field = targetClass.getDeclaredField(injectField.getField());
            field.setAccessible(true);
            field.set(target, getBean(injectField.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Class<?> getBeanClass(String className) {
        try {
            Class<?> beanClass = beanClassCache.get(className);
            if (beanClass != null) {
                return beanClass;
            }
            beanClass = Class.forName(className);
            beanClassCache.put(className, beanClass);
            return beanClass;
        } catch (ClassNotFoundException e) {
            throw new BeanCreateException(e);
        }
    }

    private Constructor<?> getConstructor(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getConstructors();
        Constructor<?> defaultConstructor = null;
        Constructor<?> requireConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor = constructor;
            }
            Autowired annotation = constructor.getAnnotation(Autowired.class);
            if (annotation != null && annotation.required()) {
                requireConstructor = constructor;
            }
        }
        if (defaultConstructor == null && requireConstructor == null) {
            throw new BeanCreateException("");
        }
        if (defaultConstructor != null && requireConstructor == null) {
            return defaultConstructor;
        }
        return requireConstructor;
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        try {
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (Exception ex) {
            throw new BeanCreateException("Is it an abstract class?", ex);
        }
    }
}
