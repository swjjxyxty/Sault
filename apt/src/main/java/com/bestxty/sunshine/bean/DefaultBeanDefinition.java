package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class DefaultBeanDefinition {

    private final String className;
    private String name;
    private boolean singleton;
    private boolean lazy;

    public DefaultBeanDefinition(String className) {
        this.className = className;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public void setName(String name) {
        this.name = name;
    }
}
