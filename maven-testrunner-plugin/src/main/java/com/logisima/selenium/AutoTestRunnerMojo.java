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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.logisima.selenium.server.NettyServer;
import com.logisima.selenium.utils.SeleniumScanner;
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
    private Integer     port;

    /**
     * Url of the application to test.
     * 
     * @parameter expression="${logisima.baseApplicationUrl}"
     * @required
     */
    private URL         baseApplicationUrl;

    /**
     * The directory for the generated WAR.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private String      outputDirectory;

    /**
     * The directory of test
     * 
     * @parameter expression="${logisima.seleniumSourceDirectory}" default-value="${project.basedir}/src/test/selenium"
     * @required
     * @readonly
     */
    private String      seleniumSourceDirectory;

    /**
     * Netty serever for testrunner
     */
    private NettyServer server;

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
        server = new NettyServer(port, outputDirectory + "/selenium", baseApplicationUrl, seleniumSourceDirectory,
                outputDirectory, 1);
        server.run();

        // get list of selenium test
        getLog().debug("Scanning " + seleniumSourceDirectory + " for selenium test.");
        SeleniumScanner scanner = new SeleniumScanner();
        scanner.scanForSeleniumTests(new File(seleniumSourceDirectory));
        List<File> tests = scanner.getTests();
        getLog().debug("Found " + tests.size() + " tests !");

        boolean testsPassed = true;
        String failedTest = "\n";
        WebClient firephoque = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
        try {
            // run all selenium test
            getLog().info("Executing " + tests.size() + " tests :");
            for (int i = 0; i < tests.size(); i++) {

                // the testrunner url to call
                URL url = TestRunnerUtils.getTestrunnerActionFullUrl(tests.get(i), baseApplicationUrl.toString(), port,
                        seleniumSourceDirectory);

                String displayName = TestRunnerUtils.getTestDisplayName(tests.get(i), seleniumSourceDirectory);

                getLog().debug("Calling " + url.toString());

                // let's do the web call !
                // get firephoque
                firephoque = TestRunnerUtils.getWebClient();
                firephoque.openWindow(url, "headless");
                firephoque.waitForBackgroundJavaScript(5 * 60 * 1000);
                int retry = 0;

                // to know if a test passed or failed, we test if there is a file testName.PASSED|FAILED.html
                while (retry < 5) {
                    if (new File(outputDirectory + "/selenium-result/" + displayName + "." + "passed.html").exists()) {
                        getLog().info(displayName + "\t\t [PASSED]     ");
                        break;
                    }
                    else if (new File(outputDirectory + "/selenium-result/" + displayName + "." + "failed.html")
                            .exists()) {
                        getLog().info(displayName + "\t\t [FAILED!]     ");
                        testsPassed = false;
                        failedTest += "\t\t" + displayName + "\n";
                        break;
                    }
                    else {
                        if (retry++ == 4) {
                            System.out.print("\t\t ... ERROR ... ?  ");
                            getLog().debug(
                                    "File " + outputDirectory + "/selenium-result/" + displayName + "."
                                            + "[passed|failed].html not found !!!");
                            testsPassed = false;
                            failedTest += "\t\t" + displayName + "\n";
                            break;
                        }
                        else {
                            Thread.sleep(1000);
                        }
                    }
                    firephoque.closeAllWindows();
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        } finally {
            getLog().debug("Stopping selenium server.");
            firephoque.closeAllWindows();
            server.interrupt();
            getLog().debug("Selenium server is stop.");
        }

        if (!testsPassed) {
            throw new MojoFailureException("There are some tests failure :" + failedTest);
        }
    }

    public void interrupt() throws InterruptedException {
        server.interrupt();
        server.join();
    }
}
