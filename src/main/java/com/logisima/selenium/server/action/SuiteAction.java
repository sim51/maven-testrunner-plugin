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
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.logisima.selenium.utils.RequestUtils;

public class SuiteAction extends ServerAction {

    public SuiteAction(HttpRequest request, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
    }

    @Override
    public void execute() {
        // we retrive data for the template
        String testPath = RequestUtils.getParameter(request, "test");
        File testFile = new File(testPath);

        // initialize velocity
        Properties props = new Properties();
        props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                ClasspathResourceLoader.class.getName());
        Velocity.init(props);
        VelocityContext context = new VelocityContext();
        // put parameter for template
        context.put("test", testFile);

        // get the template
        Template template = null;
        try {
            template = Velocity.getTemplate("com/logisima/selenium/template/suite.vm");
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
