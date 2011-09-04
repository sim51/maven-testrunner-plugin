package com.logisima.selenium.netty;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class NettyServer {

    private Integer         port;
    private Channel         channel;
    private ServerBootstrap bootstrap;
    private File            documentRoot;
    private NettyServer     server;

    public NettyServer(Integer port, String documentRoot) {
        super();
        this.port = port;
        this.documentRoot = new File(documentRoot);
    }

    public void start() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory(documentRoot));

        // Bind and start to accept incoming connections.
        channel = bootstrap.bind(new InetSocketAddress(port));
    }

    public void stop() {
        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }

}
