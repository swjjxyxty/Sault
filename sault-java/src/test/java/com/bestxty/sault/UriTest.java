package com.bestxty.sault;

import org.junit.Test;

import java.net.URI;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public class UriTest {

    @Test
    public void uriTest() throws Exception {
        URI uri = new URI("http://192.168.56.1:8000/Shadowsocks.exe");
        System.out.println(uri.toString());
        System.out.println("auth:" + uri.getAuthority());
        System.out.println("fragment:" + uri.getFragment());
        System.out.println("host:" + uri.getHost());
        System.out.println("port:" + uri.getPort());
        System.out.println("path:" + uri.getPath());
        System.out.println("query:" + uri.getQuery());
        System.out.println("userInfo:" + uri.getUserInfo());
        System.out.println("scheme:" + uri.getScheme());
        System.out.println("schememsc:" + uri.getSchemeSpecificPart());
        System.out.println("rawauth:" + uri.getRawAuthority());
        System.out.println("rawfragemtn:" + uri.getRawFragment());
        System.out.println("getRawPath:" + uri.getRawPath());
        System.out.println("getRawQuery:" + uri.getRawQuery());
        System.out.println("getRawUserInfo:" + uri.getRawUserInfo());
        System.out.println("getRawSchemeSpecificPart:" + uri.getRawSchemeSpecificPart());


    }
}
