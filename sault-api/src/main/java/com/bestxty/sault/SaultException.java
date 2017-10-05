package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class SaultException extends RuntimeException {

    public SaultException() {
    }

    public SaultException(String message) {
        super(message);
    }

    public SaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaultException(Throwable cause) {
        super(cause);
    }
}
