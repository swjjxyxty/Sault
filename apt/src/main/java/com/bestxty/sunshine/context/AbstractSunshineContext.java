package com.bestxty.sunshine.context;

import com.bestxty.sunshine.bean.BeanDefinition;
import com.bestxty.sunshine.bean.BeanDefinitionFactory;
import com.bestxty.sunshine.bean.BeanFactory;
import com.bestxty.sunshine.bean.BeanWrapper;
import com.bestxty.sunshine.bean.DefaultBeanFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public abstract class AbstractSunshineContext implements SunshineContext {

    private Map<String, BeanWrapper> beansMap = new ConcurrentHashMap<>();

    private BeanDefinitionFactory beanDefinitionFactory;

    private BeanFactory beanFactory = new DefaultBeanFactory();

    public AbstractSunshineContext(BeanDefinitionFactory beanDefinitionFactory) {
        this.beanDefinitionFactory = beanDefinitionFactory;
    }

    @Override
    public boolean beanIsExist(String id) {
        return false;
    }

    @Override
    public boolean isSingleton(String id) {
        return false;
    }

    @Override
    public Object getBean(String id) {
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

        Object bean = instanceBeanDefinition(definition);
        //注入.
        return new BeanWrapper(bean);
    }

    private Object instanceBeanDefinition(BeanDefinition definition) {
        String className = definition.getClassName();

        if (definition.getParameterTypes().isEmpty()) {
            return beanFactory.getBean(className);
        } else {
            List<Object> args = getConstructArgs(definition);
            return beanFactory.getBean(className, args);
        }
    }

    private List<Object> getConstructArgs(BeanDefinition definition) {
        return Collections.emptyList();
    }

}
