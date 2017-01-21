package com.bestxty.dl.demo.bean;

/**
 * @author swjjx
 *         Created by swjjx on 2016/12/20.
 */

public class Task {

    public static final int STATE_READY = 1;
    public static final int STATE_DOWNING = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_DONE = 4;

    private String url;
    private String title;
    private String desc;
    private int state = STATE_READY;
    private Object tag;

    public Task() {
    }

    public Task(String title, String desc, String url) {
        this.title = title;
        this.desc = desc;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Task{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", state=" + state +
                '}';
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
