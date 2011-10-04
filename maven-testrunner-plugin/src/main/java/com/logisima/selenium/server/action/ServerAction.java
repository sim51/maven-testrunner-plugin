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
package com.logisima.selenium.server.action;

import java.io.File;
import java.net.URL;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Abstract class to make a server action.
 * 
 * @author bsimard
 * 
 */
public abstract class ServerAction {

    protected String             contentType;
    protected ChannelBuffer      content;
    protected HttpRequest        request;
    protected StringBuilder      chunksBuf;
    protected HttpResponseStatus status;

    protected URL                baseApplicationUrl;
    protected File               testSourceDirectory;
    protected File               outputDirectory;

    /**
     * Constructor
     */
    public ServerAction(HttpRequest request, StringBuilder chunksBuf, URL baseApplicationUrl, File testSourceDirectory,
            File outputDirectory) {
        super();
        this.status = HttpResponseStatus.OK;
        this.request = request;
        this.chunksBuf = chunksBuf;
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = testSourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    /**
     * Perform the action !
     */
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
