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

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.logisima.selenium.server.action.ListTestAction;
import com.logisima.selenium.server.action.ServerAction;
import com.logisima.selenium.server.action.StaticAction;
import com.logisima.selenium.server.action.SuiteAction;
import com.logisima.selenium.server.action.TestAction;
import com.logisima.selenium.server.action.TestResultAction;

public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    /**
     * The request
     */
    private HttpRequest request;

    /**
     * Document of the server
     */
    private File        documentRoot;

    /**
     * The url of the application to test
     */
    private URL         baseApplicationUrl;

    /**
     * Directory of test source
     */
    private File        testSourceDirectory;

    /**
     * maven target directory
     */
    private File        outputDirectory;

    /**
     * The constructor.
     * 
     * @param documentRoot
     * @param baseApplicationUrl
     * @param testSourceDirectory
     * @param outputDirectory
     */
    public HttpRequestHandler(File documentRoot, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super();
        this.documentRoot = documentRoot;
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = testSourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpRequest request = this.request = (HttpRequest) e.getMessage();

        if (is100ContinueExpected(request)) {
            send100Continue(e);
        }

        // do the response
        writeResponse(e);

    }

    /**
     * Method that generate the serveur response.
     * 
     * @param e
     * @throws IOException
     */
    private void writeResponse(MessageEvent e) throws IOException {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);
        ServerAction action = null;

        // The default action, list all selenium test
        if (request.getUri().equals("/")) {
            action = new ListTestAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
        }
        // action that generate the selenium script
        else if (request.getUri().startsWith("/test/")) {
            action = new TestAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
        }
        // action that generate the suite file for the testrunner
        else if (request.getUri().startsWith("/suite")) {
            action = new SuiteAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
        }
        // action that generate the result file
        else if (request.getUri().startsWith("/testresult/")) {
            action = new TestResultAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
        }
        else {
            // It's a static file
            action = new StaticAction(request, documentRoot, baseApplicationUrl, testSourceDirectory, outputDirectory);
        }

        action.execute();

        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, action.getStatus());
        response.setHeader(CONTENT_TYPE, action.getContentType());
        response.setContent(action.getContent());

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        }

        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}