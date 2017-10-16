package com.bestxty.sunshine.bean;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */
@Singleton
@Named

public interface BeanDefinition {

    String getId();

    String getClassName();

    List<String> getParameterTypes();

    boolean isSingleton();

    boolean isLazy();
}
