package com.bestxty.sault.event.hunter;

import com.bestxty.sault.Hunter;
import com.bestxty.sault.event.Event;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public abstract class HunterEvent extends Event {

    /**
     * Constructs a prototypical Event.
     *
     * @param hunter The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public HunterEvent(Hunter hunter) {
        super(hunter);
    }
}
