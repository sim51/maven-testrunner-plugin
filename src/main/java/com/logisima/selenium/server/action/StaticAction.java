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

public class StaticAction extends ServerAction {

    private File documentRoot;

    public StaticAction(HttpRequest request, File documentRoot, URL baseApplicationUrl, File testSourceDirectory,
            File outputDirectory) {
        super(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
        this.documentRoot = documentRoot;
    }

    @Override
    public void execute() {
        this.setContentType();
        try {
            if (request.getUri().endsWith(".png")) {
                File image = new File(documentRoot.getAbsolutePath() + request.getUri());
                this.content = ChannelBuffers.copiedBuffer(FileUtils.readFileToByteArray(image));
            }
            else {
                // Process the request
                StringBuilder buf = new StringBuilder();
                buf.setLength(0);
                Scanner scanner = null;
                try {
                    String fileName = documentRoot.getAbsolutePath() + request.getUri();
                    String NL = System.getProperty("line.separator");
                    scanner = new Scanner(new FileInputStream(fileName), "utf-8");
                    while (scanner.hasNextLine()) {
                        buf.append(scanner.nextLine() + NL);
                    }
                } finally {
                    scanner.close();
                }
                this.content = ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8);
            }
        } catch (FileNotFoundException e) {
            this.status = HttpResponseStatus.NOT_FOUND;
        } catch (IOException e) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
    }

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
        else {
            this.contentType = "text/html; charset=UTF-8";
        }
    }

}
