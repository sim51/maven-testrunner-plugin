package com.logisima.selenium.server.action;

import java.io.File;
import java.net.URL;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public abstract class ServerAction {

    protected String             contentType;
    protected ChannelBuffer      content;
    protected HttpRequest        request;
    protected HttpResponseStatus status;

    protected URL                baseApplicationUrl;
    protected File               testSourceDirectory;
    protected File               outputDirectory;

    /**
     * Constructor
     */
    public ServerAction(HttpRequest request, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super();
        this.status = HttpResponseStatus.OK;
        this.request = request;
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = testSourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    public abstract void execute();

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @return the status
     */
    public HttpResponseStatus getStatus() {
        return status;
    }

    /**
     * @return the content
     */
    public ChannelBuffer getContent() {
        return content;
    }

}
