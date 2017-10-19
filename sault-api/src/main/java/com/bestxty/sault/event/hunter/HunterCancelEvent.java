package com.bestxty.sault.event.hunter;

import com.bestxty.sault.Hunter;
import com.bestxty.sault.event.hunter.HunterEvent;

/**
 * @author xty
 * Created by xty on 2017/10/4.
 */
public class HunterCancelEvent extends HunterEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param hunter The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public HunterCancelEvent(Hunter hunter) {
        super(hunter);
    }
}
