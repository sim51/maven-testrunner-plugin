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

/**
 * Utils class to get information from a request.
 * 
 * @author bsimard
 * 
 */
public class RequestUtils {

    /**
     * Retrive a GET parameter from the request.
     * 
     * @param request
     * @param param
     * @return the value of the parameter, <code>null</code> if <code>param</code> does'nt exist.
     */
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

    /**
     * Return all POST param of a request.
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
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
