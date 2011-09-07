package com.logisima.selenium.server;

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.File;
import java.net.URL;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerPipelineFactory implements ChannelPipelineFactory {

    private File documentRoot;
    private URL  baseApplicationUrl;
    private File testSourceDirectory;
    private File outputDirectory;

    public HttpServerPipelineFactory(File documentRoot, URL baseApplicationUrl, File testSourceDirectory,
            File outputDirectory) {
        this.documentRoot = documentRoot;
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = testSourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    public ChannelPipeline getPipeline() throws Exception {

        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());

        // Uncomment the following line if you don't want to handle HttpChunks.
        // pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler", new HttpRequestHandler(documentRoot, baseApplicationUrl, testSourceDirectory,
                outputDirectory));

        return pipeline;
    }
}
