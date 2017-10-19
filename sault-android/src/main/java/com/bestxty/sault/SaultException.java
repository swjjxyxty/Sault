package com.bestxty.sault;

/**
 * @author xty
 *         Created by xty on 2016/12/8.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SaultException extends RuntimeException {

    public SaultException() {
        super();
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
