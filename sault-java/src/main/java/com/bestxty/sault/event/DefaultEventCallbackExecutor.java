package com.bestxty.sault.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bestxty.sault.utils.Utils.error;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class DefaultEventCallbackExecutor extends Thread implements EventCallbackExecutor {

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private volatile AtomicBoolean exit = new AtomicBoolean(false);

    public DefaultEventCallbackExecutor(String threadName) {
        super(threadName);
        start();
    }

    @Override
    public void execute(Runnable task) {
        queue.add(task);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable runnable = queue.take();
                runnable.run();
            } catch (Exception ex) {
                error(ex.getMessage(), ex);
            }
            if (exit.get()) {
                break;
            }
        }
    }

    @Override
    public void shutdown() {
        exit.set(true);
        this.interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            error(e.getMessage(), e);
        }
    }
}
