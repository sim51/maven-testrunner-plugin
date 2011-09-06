package com.logisima.selenium;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.logisima.selenium.netty.NettyServer;

/**
 * Run all selenium test.
 * 
 * @goal run
 */
public class TestRunnerMojo extends AbstractMojo {

    /**
     * The port for selenium server.
     * 
     * @parameter expression="${logisima.port}" default-value="7777"
     */
    private Integer port;

    /**
     * Url of the application to test.
     * 
     * @parameter expression="${logisima.baseApplicationUrl}"
     * @required
     */
    private URL     baseApplicationUrl;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String  outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {

        // we copy selenium app to target/selenium
        File seleniumTarget = new File(outputDirectory + "selenium");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);

        // starting selenium server.
        NettyServer server = new NettyServer(port, outputDirectory + "selenium");

        // get a web client
        WebClient firefoque = TestRunnerUtils.getWebClient();

        // here we can call something like
        // http://localhost:9000/public/test-runner/selenium/TestRunner.html?baseUrl=http://localhost:9000&test=/@tests/NotLoggedUser.test.html.suite&auto=true&resultsUrl=/@tests/NotLoggedUser.test.html

    }
}
