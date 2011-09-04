package com.logisima.selenium.netty;

import junit.framework.TestCase;

import org.junit.Test;

public class NettyServerTest extends TestCase {

    @Test
    public void testStartServer() {
        NettyServer server = new NettyServer(7777, "/home/bsimard/tmp/selenium");
        server.start();
        assertEquals(true, true);
    }
}
