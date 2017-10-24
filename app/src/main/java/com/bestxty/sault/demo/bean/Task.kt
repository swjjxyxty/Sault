package com.bestxty.sault.demo.bean;

import com.bestxty.sault.Sault;

/**
 * @author swjjx
 *         Created by swjjx on 2016/12/20.
 */

class Task {
    companion object {
        const val STATE_READY: Int = 1
        const val STATE_DOWNING: Int = 2
        const val STATE_PAUSE: Int = 3
        const val STATE_DONE: Int = 4
    }

    var url: String? = null
    var title: String? = null
    var desc: String? = null
    var state: Int = STATE_READY
    var tag: Any? = null
    var priority: Sault.Priority? = null

    constructor() {}


    constructor(title: String, desc: String, url: String, priority: Sault.Priority) {
        this.title = title
        this.desc = desc
        this.url = url
        this.priority = priority
    }

    override fun toString(): String {
        return "Task{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", state=" + state +
                '}'
    }


}
