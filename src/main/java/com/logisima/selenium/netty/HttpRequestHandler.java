package com.logisima.selenium.netty;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffers;
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
import org.jboss.netty.util.CharsetUtil;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * 
 * @version $Rev: 2368 $, $Date: 2010-10-18 17:19:03 +0900 (Mon, 18 Oct 2010) $
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    private HttpRequest request;
    private boolean     readingChunks;
    private File        documentRoot;

    public HttpRequestHandler(File documentRoot) {
        super();
        this.documentRoot = documentRoot;
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

        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        String responseType = "text";
        // content-type
        if (request.getUri().endsWith(".css")) {
            response.setHeader(CONTENT_TYPE, "text/css; charset=utf-8");
        }
        else if (request.getUri().endsWith(".js")) {
            response.setHeader(CONTENT_TYPE, "application/javascript");
        }
        else if (request.getUri().endsWith(".html")) {
            response.setHeader(CONTENT_TYPE, "text/html; charset=utf-8");
        }
        if (request.getUri().endsWith(".txt") || request.getUri().endsWith(".log")) {
            response.setHeader(CONTENT_TYPE, "text/plain; charset=utf-8");
        }
        else if (request.getUri().endsWith(".png")) {
            response.setHeader(CONTENT_TYPE, "image/png; charset=UTF-8");
            responseType = "file";
        }
        else {
            response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        }

        if (responseType.equals("text")) {
            /** Buffer that stores the response content */
            StringBuilder buf = new StringBuilder();
            buf.setLength(0);

            // Process the request
            String fileName = documentRoot.getAbsolutePath() + request.getUri();
            String NL = System.getProperty("line.separator");
            Scanner scanner = new Scanner(new FileInputStream(fileName), "utf-8");
            try {
                while (scanner.hasNextLine()) {
                    buf.append(scanner.nextLine() + NL);
                }
            } finally {
                scanner.close();
            }
            response.setContent(ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        }
        else {
            File image = new File(documentRoot.getAbsolutePath() + request.getUri());
            response.setContent(ChannelBuffers.copiedBuffer(FileUtils.readFileToByteArray(image)));
        }

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