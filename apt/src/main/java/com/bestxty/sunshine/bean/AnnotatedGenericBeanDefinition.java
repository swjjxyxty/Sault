package com.bestxty.sunshine.bean;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private MethodMetadata factoryMethodMetadata;



    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return factoryMethodMetadata;
    }
}
