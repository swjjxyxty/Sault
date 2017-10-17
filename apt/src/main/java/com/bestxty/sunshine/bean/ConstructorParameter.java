package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public final class ConstructorParameter {

    private String name;

    private String type;


    public ConstructorParameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public ConstructorParameter(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
