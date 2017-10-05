package com.bestxty.sault.event;

import java.util.EventListener;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public interface EventCallback<E extends Event> extends EventListener {


    void onEvent(E event);


}
