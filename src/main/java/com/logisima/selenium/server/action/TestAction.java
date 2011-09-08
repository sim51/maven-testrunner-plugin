package com.logisima.selenium.server.action;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
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

import com.logisima.selenium.bean.TestScenario;
import com.logisima.selenium.utils.RequestUtils;
import com.logisima.selenium.utils.SeleniumUtils;

public class TestAction extends ServerAction {

    public TestAction(HttpRequest request, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super(request, baseApplicationUrl, outputDirectory, outputDirectory);
    }

    @Override
    public void execute() {
        try {
            // we retrive data for the template
            String testPath = RequestUtils.getParameter(request, "test");
            File testFile = new File(testPath);
            List<TestScenario> tests;
            tests = SeleniumUtils.parseTestFile(testFile);

            // initialize velocity
            Properties props = new Properties();
            props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
            props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                    ClasspathResourceLoader.class.getName());
            Velocity.init(props);
            VelocityContext context = new VelocityContext();
            // put parameter for template
            context.put("tests", tests);

            // get the template
            Template template = null;
            try {
                template = Velocity.getTemplate("com/logisima/selenium/template/test.vm");
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                // create the response
                this.contentType = "text/html; charset=utf-8";
                this.content = ChannelBuffers.copiedBuffer(sw.toString(), CharsetUtil.UTF_8);
            } catch (ResourceNotFoundException rnfe) {
                this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                this.contentType = "text/html; charset=utf-8";
                this.content = ChannelBuffers.copiedBuffer(rnfe.toString(), CharsetUtil.UTF_8);
            } catch (ParseErrorException pee) {
                this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                this.contentType = "text/html; charset=utf-8";
                this.content = ChannelBuffers.copiedBuffer(pee.toString(), CharsetUtil.UTF_8);
            } catch (MethodInvocationException mie) {
                this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                this.contentType = "text/html; charset=utf-8";
                this.content = ChannelBuffers.copiedBuffer(mie.toString(), CharsetUtil.UTF_8);
            } catch (Exception e) {
                this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                this.contentType = "text/html; charset=utf-8";
                this.content = ChannelBuffers.copiedBuffer(e.toString(), CharsetUtil.UTF_8);
            }

            // render template
        } catch (IOException e1) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            this.contentType = "text/html; charset=utf-8";
            this.content = ChannelBuffers.copiedBuffer(e1.toString(), CharsetUtil.UTF_8);
        }

    }
}
