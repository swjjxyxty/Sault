package com.bestxty.sault.downloader;

import com.bestxty.sault.utils.IOUtils;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author xty
 *         Created by xty on 2017/10/4.
 */
public class HttpURLConnectionDownloaderTest {
    private Downloader downloader;
    private URI uri;

    @Before
    public void setup() throws URISyntaxException {
        downloader = new HttpURLConnectionDownloader();
        uri = new URI("http://192.168.56.1:8000/Shadowsocks.exe");
    }

    @Test
    public void load() throws Exception {
        Response response = downloader.load(uri, Collections.<String, String>emptyMap(), 0, -1);
        assertEquals(1198080, response.getContentLength());
        IOUtils.closeQuietly(response);
    }

    @Test
    public void loadWithIncorrectStartPosition() throws Exception {
        Response response = downloader.load(uri, Collections.<String, String>emptyMap(), -1, -1);
        assertEquals(1198080, response.getContentLength());
        IOUtils.closeQuietly(response);
    }

    @Test
    public void loadWithIncorrectEndPosition() throws Exception {
        Response response = downloader.load(uri, Collections.<String, String>emptyMap(), 1000, 80);
        assertEquals(1198080 - 1000, response.getContentLength());
        IOUtils.closeQuietly(response);
    }

    @Test
    public void shouldRetry() throws Exception {
        assertTrue(downloader.shouldRetry(Network.CONNECTED));
        assertFalse(downloader.shouldRetry(Network.AIRPLANE_MODE));

    }

    @Test
    public void supportBreakPoint() throws Exception {
        assertTrue(downloader.supportBreakPoint());
    }

    @Test
    public void getRetryCount() throws Exception {
        assertEquals(2, downloader.getRetryCount());
    }

}