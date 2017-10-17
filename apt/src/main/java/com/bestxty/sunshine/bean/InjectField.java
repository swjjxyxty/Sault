package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class InjectField {

    private final String field;

    private final String name;

    private final String type;

    public InjectField(String field, String name, String type) {
        this.field = field;
        this.name = name;
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
