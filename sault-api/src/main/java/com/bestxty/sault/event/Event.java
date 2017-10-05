package com.bestxty.sault.event;

import java.util.EventObject;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public abstract class Event extends EventObject {


    /**
     * System time when the event happened
     */
    private final long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public Event(Object source) {
        super(source);

        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
