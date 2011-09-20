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
     * Execute method of the Mojo.
     */
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
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        } finally {
            server.shutdown();
        }
    }
}
