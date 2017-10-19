package com.bestxty.sault;

import com.bestxty.sault.downloader.HttpURLConnectionDownloader;
import com.bestxty.sault.event.DefaultEventCallbackExecutor;
import com.bestxty.sault.event.Event;
import com.bestxty.sault.event.EventCallback;
import com.bestxty.sault.event.HunterEventDispatcher;
import com.bestxty.sault.event.task.TaskCompleteEvent;
import com.bestxty.sault.event.task.TaskProgressEvent;
import com.bestxty.sault.event.task.TaskStartEvent;

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
        HunterEventDispatcher hunterEventDispatcher = new HunterEventDispatcher(new DefaultEventCallbackExecutor("Hunter-Event-Dispatcher"),
                new SimpleSaultExecutorService(), new HttpURLConnectionDownloader());
        Sault sault = new Sault(hunterEventDispatcher);
        Task task = new HunterTask.Builder()
                .uri("http://192.168.56.1:8000/Shadowsocks.exe")
                .target(new File("D:\\Shadowsocks.exe"))
                .callback(new EventCallback<TaskStartEvent>() {
                    @Override
                    public void onEvent(TaskStartEvent event) {
                        System.err.println("---event = " + event);
                    }
                })
                .callback(new EventCallback<TaskCompleteEvent>() {
                    @Override
                    public void onEvent(TaskCompleteEvent event) {
                        System.err.println("---event = " + event);
                    }
                })
                .callback(new EventCallback<TaskProgressEvent>() {
                    @Override
                    public void onEvent(TaskProgressEvent event) {
                        System.err.println("event.getProgress() = " + event.getProgress());
                    }
                })
                .build();
        sault.submit(task);

        Thread.sleep(5000);

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