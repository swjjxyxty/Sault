package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public final class BeanWrapper {
    private final Object bean;

    private boolean singleton;

    public BeanWrapper(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isSingleton() {
        return singleton;
    }
}
