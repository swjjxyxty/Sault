package com.bestxty.sunshine.bean;

import java.util.List;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public interface BeanFactory {

    Object getBean(String className);

    Object getBean(String className, List<Object> args);
}
