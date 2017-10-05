package com.bestxty.sault;

import android.Manifest;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.Closeable;
import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */

public class UtilsTest extends ApplicationTestCase {

    @Test
    public void log() throws Exception {
        Utils.log("log test");
    }

    @Test
    public void log1() throws Exception {
        Utils.log("error test", new RuntimeException("ex"));
    }

    @Test
    public void closeQuietly() throws Exception {
        Closeable closeable = Mockito.mock(Closeable.class);

        Utils.closeQuietly(closeable);
        verify(closeable, times(1)).close();
    }

    @Test
    public void createTargetFile() throws Exception {
        String path = getApplication().getCacheDir().getPath() + File.separator + "dumyFile";
        System.out.println("path = " + path);
        File file = new File(path);
        Utils.createTargetFile(file);
        assertThat(file.exists(), is(false));
    }

    @Test
    public void isAirplaneModeOn() throws Exception {
        assertFalse(Utils.isAirplaneModeOn(getContext()));
    }


    @Test
    public void hasPermission() throws Exception {

        boolean hasPermission = Utils.hasPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        assertFalse(hasPermission);
    }

}