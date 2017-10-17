package com.bestxty.sunshine.context;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public interface SunshineContext {

    Object getBean(String id);

    void close();
}
