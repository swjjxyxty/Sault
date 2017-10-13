package com.bestxty.sault;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */

public class ExceptionSaultTask extends DefaultSaultTask {
    private Exception exception;

    public ExceptionSaultTask(SaultTask task, Exception exception) {
        super(task.getSault(), task.getTag(), task.getUri(), task.getCallback(),
                task.getTarget(), task.getPriority(), task.isBreakPointEnabled());
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
