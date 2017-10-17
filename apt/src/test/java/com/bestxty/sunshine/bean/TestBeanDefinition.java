package com.bestxty.sunshine.bean;

import java.util.Collections;
import java.util.List;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class TestBeanDefinition implements BeanDefinition {

    private String name;

    private String parentName;

    private String className;

    private boolean lazy;

    private boolean singleton;

    private String initMethod;

    private List<ConstructorParameter> constructorParameters;
    private List<InjectField> injectFields;

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setConstructorParameters(List<ConstructorParameter> constructorParameters) {
        this.constructorParameters = constructorParameters;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public void setInjectFields(List<InjectField> injectFields) {
        this.injectFields = injectFields;
    }

    @Override
    public List<InjectField> getInjectFields() {
        return injectFields == null ? Collections.<InjectField>emptyList() :
                injectFields;
    }

    @Override
    public String getInitMethod() {
        return initMethod;
    }

    @Override
    public String getParentName() {
        return parentName;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public List<ConstructorParameter> getConstructorParameters() {
        return constructorParameters == null
                ? Collections.<ConstructorParameter>emptyList() : constructorParameters;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }
}
