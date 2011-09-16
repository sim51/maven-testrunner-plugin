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

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.File;
import java.net.URL;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerPipelineFactory implements ChannelPipelineFactory {

    /**
     * DocumentRoot of the server
     */
    private File documentRoot;

    /**
     * Url of the applicatoin to test
     */
    private URL  baseApplicationUrl;

    /**
     * Maven test directory
     */
    private File testSourceDirectory;

    /**
     * Maven target folder
     */
    private File outputDirectory;

    /**
     * Constructor.
     * 
     * @param documentRoot
     * @param baseApplicationUrl
     * @param testSourceDirectory
     * @param outputDirectory
     */
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
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler", new HttpRequestHandler(documentRoot, baseApplicationUrl, testSourceDirectory,
                outputDirectory));

        return pipeline;
    }
}
