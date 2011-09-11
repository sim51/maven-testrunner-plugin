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
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.logisima.selenium.utils.SeleniumUtils;

public class ListTestAction extends ServerAction {

    public ListTestAction(HttpRequest request, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
    }

    @Override
    public void execute() {
        // get list of selenium test
        SeleniumUtils selenium = new SeleniumUtils();
        selenium.scanForSeleniumTests(testSourceDirectory);

        // initialize velocity
        Properties props = new Properties();
        props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                ClasspathResourceLoader.class.getName());
        Velocity.init(props);
        VelocityContext context = new VelocityContext();
        // put parameter for template
        context.put("tests", selenium.getTests());
        context.put("baseApplicationUrl", baseApplicationUrl);
        context.put("testSourceDirectory", testSourceDirectory.getPath());
        context.put("stringUtils", new StringUtils());

        // get the template
        Template template = null;
        try {
            template = Velocity.getTemplate("com/logisima/selenium/template/list.vm");
        } catch (ResourceNotFoundException rnfe) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        } catch (ParseErrorException pee) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        } catch (MethodInvocationException mie) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }

        // render template
        StringWriter sw = new StringWriter();
        template.merge(context, sw);

        // create the response
        this.contentType = "text/html; charset=utf-8";
        this.content = ChannelBuffers.copiedBuffer(sw.toString(), CharsetUtil.UTF_8);
    }
}
