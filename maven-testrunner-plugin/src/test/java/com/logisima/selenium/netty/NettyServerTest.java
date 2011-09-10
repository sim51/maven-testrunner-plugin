package com.logisima.selenium.netty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import com.logisima.selenium.server.NettyServer;
import com.logisima.selenium.utils.TestRunnerUtils;

public class NettyServerTest extends TestCase {

    @Test
    public void testStartServer() throws MalformedURLException, MojoExecutionException {
        File seleniumTarget = new File(FileUtils.getTempDirectoryPath() + "/selenium");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);
        URL url = new URL("http://localhost:7777");

        NettyServer server = new NettyServer(7777, seleniumTarget.getPath(), url, "./src/test/resources", "./target");
        server.start();
        assertEquals(true, true);
    }
}
