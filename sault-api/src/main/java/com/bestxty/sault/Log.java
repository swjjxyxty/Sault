package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public interface Log {

    void log(String message);

    void error(String message, Throwable ex);
}
