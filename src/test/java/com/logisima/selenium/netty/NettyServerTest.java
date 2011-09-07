package com.logisima.selenium.netty;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

import com.logisima.selenium.server.NettyServer;

public class NettyServerTest extends TestCase {

    @Test
    public void testStartServer() throws MalformedURLException {
        URL url = new URL("http://localhost/");
        NettyServer server = new NettyServer(7777, "/home/bsimard/workspace_logisima/selenium-testrunner/selenium",
                url, "/home/bsimard/workspace_logisima/selenium-testrunner/src/test",
                "/home/bsimard/workspace_logisima/selenium-testrunner/target");
        server.start();
        assertEquals(true, true);
    }
}
