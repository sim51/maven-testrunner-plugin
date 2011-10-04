/**
 *  This file is part of LogiSima (http://www.logisima.com).
 *
 *  maven-testrunner-plugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  maven-testrunner-plugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with maven-testrunner-plugin. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  @author Benoît Simard
 *  @See https://github.com/sim51/maven-testrunner-plugin
 */
package com.logisima.selenium.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Class to create and maange an netty server.
 * 
 * @author bsimard
 * 
 */
public class NettyServer extends Thread {

    /**
     * port of the server.
     */
    private Integer                   port;

    /**
     * Bootsrap instance of the running server
     */
    private ServerBootstrap           bootstrap;

    /**
     * Channel group of "seleniumServer"
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup("seleniumServer");

    /**
     * Channel factory
     */
    private ChannelFactory            factory;

    /**
     * DocumentRoot of the server.
     */
    private File                      documentRoot;

    /**
     * Url of the application to test
     */
    private URL                       baseApplicationUrl;

    /**
     * Maven test folder
     */
    private File                      testSourceDirectory;

    /**
     * Maven target folder
     */
    private File                      outputDirectory;

    /**
     * Max number of retry for startup
     */
    private Integer                   nbMaxRetry;

    /**
     * Constructor.
     * 
     * @param port
     * @param documentRoot
     * @param baseApplicationUrl
     * @param testSourceDirectory
     * @param outputDirectory
     */
    public NettyServer(Integer port, String documentRoot, URL baseApplicationUrl, String testSourceDirectory,
            String outputDirectory, Integer nbMaxRetry) {
        super();
        this.port = port;
        this.documentRoot = new File(documentRoot);
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = new File(testSourceDirectory);
        this.outputDirectory = new File(outputDirectory);
        this.nbMaxRetry = nbMaxRetry;
    }

    /**
     * Method to run the server.
     */
    public void run() {
        // create the factory
        this.factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors() * 2 + 1);

        // create the boostrap server
        this.bootstrap = new ServerBootstrap(this.factory);

        // Set up the event pipeline factory.
        this.bootstrap.setPipelineFactory(new HttpServerPipelineFactory(documentRoot, baseApplicationUrl,
                testSourceDirectory, outputDirectory));

        // set options
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.connectTimeoutMillis", 100);
        bootstrap.setOption("readWriteFair", true);

        // Bind and start to accept incoming connections.ted
        Boolean isStarted = false;
        Integer nbRetry = 0;
        while (!isStarted && nbRetry < nbMaxRetry) {
            try {
                Channel channel = bootstrap.bind(new InetSocketAddress(port));
                channelGroup.add(channel);
                isStarted = true;
            } catch (ChannelException e) {
                nbRetry += 1;
            }
        }
        if (!isStarted) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Server is not started !!!");
        }
    }

    public void interrupt() {
        // Step 1: closing all channel an wait that all are closed
        Boolean shutdownOk = false;
        while (!shutdownOk) {
            shutdownOk = this.channelGroup.close().awaitUninterruptibly(100);
        }
        // Step 2: release external resource
        this.factory.releaseExternalResources();
        // Step 3: interrupt the thread
        super.interrupt();
    }
}
