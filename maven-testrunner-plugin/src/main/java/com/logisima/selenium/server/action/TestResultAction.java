package com.logisima.selenium.server.action;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
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
import com.logisima.selenium.utils.SeleniumUtils;

public class TestResultAction extends ServerAction {

    public TestResultAction(HttpRequest request, URL baseApplicationUrl, File testSourceDirectory, File outputDirectory) {
        super(request, baseApplicationUrl, testSourceDirectory, outputDirectory);
    }

    @Override
    public void execute() {
        try {
            // we retrive data for the template
            Map<String, String> parameters = RequestUtils.getPostParameters(request);
            String test = request.getUri().replaceFirst("/testresult/", "");
            String table = parameters.get("testTable.1");
            String result = parameters.get("result");

            File directoryResult = new File(outputDirectory + "/selenium-result/");
            directoryResult.mkdirs();
            File resultFile = new File(outputDirectory + "/selenium-result/" + test.replace("/", ".") + "." + result
                    + ".html");

            // initialize velocity
            Properties props = new Properties();
            props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
            props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                    ClasspathResourceLoader.class.getName());
            Velocity.init(props);
            VelocityContext context = new VelocityContext();

            // put parameter for template
            context.put("test", test);
            context.put("table", table);
            context.put("result", result);

            // get the template
            Template template = null;
            StringWriter sw = new StringWriter();
            try {
                template = Velocity.getTemplate("com/logisima/selenium/template/result.vm");
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

            // write result file into ./target/selenium-result/mytest.FAILED.html
            SeleniumUtils.writeContent(sw.toString(), resultFile);

            // render template
        } catch (IOException e1) {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            this.contentType = "text/html; charset=utf-8";
            this.content = ChannelBuffers.copiedBuffer("Done", CharsetUtil.UTF_8);
        }

    }
}
