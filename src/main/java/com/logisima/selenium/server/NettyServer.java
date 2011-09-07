package com.logisima.selenium.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class NettyServer {

    private Integer         port;
    private Channel         channel;
    private ServerBootstrap bootstrap;
    private File            documentRoot;
    private URL             baseApplicationUrl;
    private File            testSourceDirectory;
    private File            outputDirectory;
    private NettyServer     server;

    public NettyServer(Integer port, String documentRoot, URL baseApplicationUrl, String testSourceDirectory,
            String outputDirectory) {
        super();
        this.port = port;
        this.documentRoot = new File(documentRoot);
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = new File(testSourceDirectory);
        this.outputDirectory = new File(outputDirectory);
    }

    public void start() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory(documentRoot, baseApplicationUrl,
                testSourceDirectory, outputDirectory));

        // Bind and start to accept incoming connections.
        channel = bootstrap.bind(new InetSocketAddress(port));
    }

    public void stop() {
        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }

}
