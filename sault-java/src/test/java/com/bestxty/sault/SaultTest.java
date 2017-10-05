package com.bestxty.sault;

import com.bestxty.sault.downloader.HttpURLConnectionDownloader;
import com.bestxty.sault.event.DefaultEventCallbackExecutor;
import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.HunterEventDispatcher;
import com.bestxty.sault.event.TaskEventDispatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class SaultTest {
    private Task task;

    @Before
    public void setUp() throws Exception {
        task = new HunterTask.Builder()
                .uri("http://192.168.56.1:8000/Shadowsocks.exe")
                .build();
    }

    @Test
    public void submit() throws Exception {
        TaskEventDispatcher taskEventDispatcher = new TaskEventDispatcher(new DefaultEventCallbackExecutor("Task-Event-Dispatcher"));
        HunterEventDispatcher hunterEventDispatcher = new HunterEventDispatcher(new DefaultEventCallbackExecutor("Hunter-Event-Dispatcher"),
                new SimpleSaultExecutorService(), new HttpURLConnectionDownloader());
        Sault sault = new Sault(taskEventDispatcher, hunterEventDispatcher);
        Task task = new HunterTask.Builder()
                .uri("http://192.168.56.1:8000/Shadowsocks.exe")
                .target(new File("D:\\Shadowsocks.exe"))
                .callback(new EventCallback<Event>() {
                    @Override
                    public void onEvent(Event event) {

                    }
                })
                .build();
        sault.submit(task);

        Thread.sleep(10000);

        sault.pause(task.getTaskId());


    }

    @Test
    public void pause() throws Exception {

    }

    @Test
    public void cancel() throws Exception {

    }

    @Test
    public void resume() throws Exception {

    }

}