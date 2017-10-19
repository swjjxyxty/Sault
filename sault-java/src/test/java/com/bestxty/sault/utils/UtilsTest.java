package com.bestxty.sault.utils;

import com.bestxty.sault.HunterTask;
import com.bestxty.sault.Priority;

import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class UtilsTest {
    @Test
    public void buildTaskId() throws Exception {
        System.out.println(Utils.buildTaskId(new HunterTask.Builder()
                .uri(new URI("http://192.168.56.1:8000/2/1/Shadowsocks.exe?xx=xx1"))
                .priority(Priority.LOW)
                .target(new File(""))
                .breakPointEnabled(false)
                .multiThreadEnabled(true)
                .retryEnabled(true)
                .build()));
    }

}