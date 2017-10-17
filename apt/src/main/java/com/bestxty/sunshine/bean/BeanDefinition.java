package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */
public interface BeanDefinition {

    void setParentName(String parentName);

    String getParentName();

    void setBeanClassName(String beanClassName);

    String getBeanClassName();

    void setLazyInit(boolean lazyInit);

    boolean isLazyInit();

    void setDependsOn(String... dependsOn);

    String[] getDependsOn();

    void setFactoryBeanName(String factoryBeanName);

    String getFactoryBeanName();

    void setFactoryMethodName(String factoryMethodName);

    String getFactoryMethodName();

    void setSingleton(boolean singleton);

    boolean isSingleton();

}
