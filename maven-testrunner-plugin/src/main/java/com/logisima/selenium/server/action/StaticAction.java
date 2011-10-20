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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

/**
 * Server action to serve static file.
 * 
 * @author bsimard
 * 
 */
public class StaticAction extends ServerAction {

    /**
     * The documentRoot folder of the server.
     */
    private File documentRoot;

    /**
     * Constructor.
     * 
     * @param request
     * @param chunksBuf
     * @param documentRoot
     * @param baseApplicationUrl
     * @param testSourceDirectory
     * @param outputDirectory
     */
    public StaticAction(HttpRequest request, StringBuilder chunksBuf, File documentRoot, URL baseApplicationUrl,
            File testSourceDirectory, File outputDirectory) {
        super(request, chunksBuf, baseApplicationUrl, testSourceDirectory, outputDirectory);
        this.documentRoot = documentRoot;
    }

    @Override
    public void execute() {
        this.setContentType();
        try {
            if (request.getUri().endsWith(".png") | request.getUri().endsWith(".gif")) {
                File image = new File(documentRoot.getAbsolutePath() + request.getUri());
                this.content = ChannelBuffers.copiedBuffer(FileUtils.readFileToByteArray(image));
            }
            else {
                // Process the request
                StringBuilder buf = new StringBuilder();
                buf.setLength(0);
                Scanner scanner = null;
                String fileName = documentRoot.getAbsolutePath() + request.getUri().split("[?]")[0];
                String NL = System.getProperty("line.separator");
                scanner = new Scanner(new FileInputStream(fileName), "utf-8");
                while (scanner.hasNextLine()) {
                    buf.append(scanner.nextLine() + NL);
                }
                this.content = ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8);
            }
        } catch (FileNotFoundException e) {
            this.status = HttpResponseStatus.NOT_FOUND;
            this.content = ChannelBuffers.copiedBuffer(e.toString(), CharsetUtil.UTF_8);
        } catch (IOException e) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            this.content = ChannelBuffers.copiedBuffer(e.toString(), CharsetUtil.UTF_8);
        }
    }

    /**
     * Method to set the content-type of the response.
     */
    private void setContentType() {
        // content-type
        if (request.getUri().endsWith(".css")) {
            this.contentType = "text/css; charset=utf-8";
        }
        else if (request.getUri().endsWith(".js")) {
            this.contentType = "application/javascript";
        }
        else if (request.getUri().endsWith(".html")) {
            this.contentType = "text/html; charset=utf-8";
        }
        if (request.getUri().endsWith(".txt") || request.getUri().endsWith(".log")) {
            this.contentType = "text/plain; charset=utf-8";
        }
        else if (request.getUri().endsWith(".png")) {
            this.contentType = "image/png; charset=UTF-8";
        }
        else if (request.getUri().endsWith(".gif")) {
            this.contentType = "image/gif; charset=UTF-8";
        }
        else {
            this.contentType = "text/html; charset=UTF-8";
        }
    }

}
