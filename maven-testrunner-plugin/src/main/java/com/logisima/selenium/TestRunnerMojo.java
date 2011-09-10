package com.logisima.selenium;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.logisima.selenium.server.NettyServer;
import com.logisima.selenium.server.NettyServerScanner;
import com.logisima.selenium.utils.TestRunnerUtils;

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
     * @readonly
     */
    private String  outputDirectory;

    /**
     * The directory of test
     * 
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private String  testSourceDirectory;

    /**
     * Decides whether to wait after the server is started or to return the execution flow to the user.
     * 
     * @parameter default-value = "true"
     * @required
     */
    private boolean wait;

    public void execute() throws MojoExecutionException, MojoFailureException {

        // we copy selenium app to target/selenium
        File seleniumTarget = new File(outputDirectory + "/selenium");
        getLog().debug("Selenium testrunning will be deployed into " + seleniumTarget.getPath() + " directory");
        TestRunnerUtils.copySeleniumToDir(seleniumTarget);
        getLog().debug("Selenium testrunning has been deployed");

        // Start the server
        NettyServer server = new NettyServer(port, outputDirectory + "/selenium", baseApplicationUrl,
                testSourceDirectory, outputDirectory);
        server.run();

        getLog().info("Selenium testrunner server is running on port " + port);
        getLog().info("Press Ctrl-C to stop the server...");

        // A little hack to avoid exiting process ...
        NettyServerScanner scanner = new NettyServerScanner(this, port);
        scanner.start();
        try {
            while (scanner.isAlive()) {
                Thread.currentThread().sleep(100);
            }
        } catch (InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        }

    }
}
