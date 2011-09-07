package com.logisima.selenium.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.logisima.selenium.server.action.ListTestAction;
import com.logisima.selenium.server.action.ServerAction;
import com.logisima.selenium.server.action.StaticAction;
import com.logisima.selenium.server.action.SuiteAction;
import com.logisima.selenium.server.action.TestAction;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * 
 * @version $Rev: 2368 $, $Date: 2010-10-18 17:19:03 +0900 (Mon, 18 Oct 2010) $
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    private HttpRequest request;
    private File        documentRoot;
    private URL         baseApplicationUrl;
    private File        testSourceDirectory;
    private File        outputDirectory;

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

    private void writeResponse(MessageEvent e) throws IOException {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);
        ServerAction action = null;

        if (request.getUri().contains(".action")) {
            if (request.getUri().contains("list.action")) {
                action = new ListTestAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
            }
            else if (request.getUri().contains("list.action")) {
                action = new TestAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
            }
            else if (request.getUri().contains("suite.action")) {
                action = new SuiteAction(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
            }
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

        // Encode the cookie.
        String cookieString = request.getHeader(COOKIE);
        if (cookieString != null) {
            CookieDecoder cookieDecoder = new CookieDecoder();
            Set<Cookie> cookies = cookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder cookieEncoder = new CookieEncoder(true);
                for (Cookie cookie : cookies) {
                    cookieEncoder.addCookie(cookie);
                }
                response.addHeader(SET_COOKIE, cookieEncoder.encode());
            }
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