package com.bestxty.sault.event.hunter;

import com.bestxty.sault.Hunter;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class HunterProgressEvent extends HunterEvent {

    private long finishedSize;

    public HunterProgressEvent(Hunter hunter, long finishedSize) {
        super(hunter);
        this.finishedSize = finishedSize;
    }

    public long getFinishedSize() {
        return finishedSize;
    }
}
