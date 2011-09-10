package com.logisima.selenium.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

public class RequestUtils {

    public static String getParameter(HttpRequest request, String param) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
        Map<String, List<String>> params = queryStringDecoder.getParameters();
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                if (key.equals(param)) {
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        return val;
                    }
                }
            }
        }
        return null;
    }

    public static Map<String, String> getPostParameters(HttpRequest request) throws UnsupportedEncodingException {
        Map<String, String> postArgs = new HashMap<String, String>();
        ChannelBuffer cbContent = request.getContent();
        String content = cbContent.toString(CharsetUtil.UTF_8);
        String[] arguments = content.split("&");
        for (int i = 1; i < arguments.length; i++) {
            String[] arg = arguments[i].split("=");
            String name = arg[0];
            String value = arg[1];
            postArgs.put(name, URLDecoder.decode(value, "UTF-8"));
        }
        return postArgs;
    }
}
