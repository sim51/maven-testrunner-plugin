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
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.util.StringUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.logisima.selenium.server.NettyServer;
import com.logisima.selenium.utils.SeleniumUtils;
import com.logisima.selenium.utils.TestRunnerUtils;

/**
 * Run all selenium test.
 * 
 * @goal auto-run
 */
public class AutoTestRunnerMojo extends AbstractMojo {

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
        NettyServer server = new NettyServer(port, outputDirectory + "selenium", baseApplicationUrl,
                testSourceDirectory, outputDirectory);
        server.run();

        // get firephoque
        WebClient firephoque = TestRunnerUtils.getWebClient();

        // get list of selenium test
        SeleniumUtils selenium = new SeleniumUtils();
        selenium.scanForSeleniumTests(new File(testSourceDirectory));
        List<File> tests = selenium.getTests();

        try {
            for (int i = 0; i < tests.size(); i++) {
                String testFile = StringUtils.sub(tests.get(i).getPath(), testSourceDirectory, "");
                URL url = SeleniumUtils.testFileToTestrunnerUri(testFile, baseApplicationUrl.toString(), port);
                firephoque.openWindow(url, "headless");
                firephoque.waitForBackgroundJavaScript(5 * 60 * 1000);
                int retry = 0;
                while (retry < 5) {
                    if (new File(outputDirectory + "/selenium-result/" + testFile.replace("/", ".") + "."
                            + "passed.html").exists()) {
                        getLog().info(testFile.replace("/", ".") + "     PASSED     ");
                        break;
                    }
                    else if (new File(outputDirectory + "/selenium-result/" + testFile.replace("/", ".") + "."
                            + "failed.html").exists()) {
                        getLog().info(testFile.replace("/", ".") + "     FAILED!     ");
                        break;
                    }
                    else {
                        if (retry++ == 4) {
                            System.out.print("ERROR   ?  ");
                            break;
                        }
                        else {
                            Thread.currentThread().sleep(1000);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
