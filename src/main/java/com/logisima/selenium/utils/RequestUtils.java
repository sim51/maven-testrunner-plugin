package com.logisima.selenium.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

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
}
