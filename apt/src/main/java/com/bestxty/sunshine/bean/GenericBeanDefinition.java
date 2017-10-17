package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class GenericBeanDefinition extends AbstractBeanDefinition {

    private String parentName;

    @Override
    public String getParentName() {
        return parentName;
    }

    @Override
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof GenericBeanDefinition && super.equals(other)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Generic bean");
        if (this.parentName != null) {
            sb.append(" with parent '").append(this.parentName).append("'");
        }
        sb.append(": ").append(super.toString());
        return sb.toString();
    }
}
