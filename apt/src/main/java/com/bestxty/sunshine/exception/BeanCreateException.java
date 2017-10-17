package com.bestxty.sunshine.exception;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class BeanCreateException extends RuntimeException {
    public BeanCreateException() {
        super();
    }

    public BeanCreateException(String s) {
        super(s);
    }

    public BeanCreateException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BeanCreateException(Throwable throwable) {
        super(throwable);
    }
}
