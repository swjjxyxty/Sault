package com.bestxty.sunshine.bean;

import com.bestxty.sunshine.annotation.Component;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */
@Component
public class Object3 {

    @Inject
    @Named("object3Name")
    private String name;

    @Inject
    @Named("java.lang.Integer")
    private int age;

    @Inject
    @Named("object1")
    private Object1 object1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Object1 getObject1() {
        return object1;
    }

    public void setObject1(Object1 object1) {
        this.object1 = object1;
    }
}
